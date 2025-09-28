package com.epic.finance.service;

import com.epic.finance.client.AuthClient;
import com.epic.finance.dto.transaction.CreateTransactionDto;
import com.epic.finance.dto.transaction.TransactionDto;
import com.epic.finance.dto.transaction.UpdateTransactionDto;
import com.epic.finance.entity.Account;
import com.epic.finance.entity.Category;
import com.epic.finance.entity.Transaction;
import com.epic.finance.exception.account.AccountNotFoundException;
import com.epic.finance.exception.transaction.CategoryNotFoundException;
import com.epic.finance.exception.transaction.TransactionNotFoundException;
import com.epic.finance.mapper.AccountMapper;
import com.epic.finance.mapper.CategoryMapper;
import com.epic.finance.mapper.TransactionMapper;
import com.epic.finance.repository.AccountRepository;
import com.epic.finance.repository.CategoryRepository;
import com.epic.finance.repository.TransactionRepository;
import com.epic.shared.dto.UserInfoDto;
import com.epic.shared.enums.CategoryType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


/**
 * Serviço responsável pelas operações de negócio relacionadas à entidade {@link Transaction}.
 * <p>
 * Este serviço encapsula todas as operações CRUD, bem como consultas avançadas com filtros, paginação
 * e ordenação. Ele também integra com o {@link AuthClient} para obter informações do utilizador
 * e com os mappers para converter entre entidades e DTOs.
 * </p>
 *
 * Funcionalidades:
 * <ul>
 *     <li>Obter transações de um utilizador, com filtros por conta, categoria e intervalo de datas.</li>
 *     <li>Obter uma transação específica pelo seu ID.</li>
 *     <li>Criar novas transações, garantindo que a conta e categoria pertençam ao utilizador.</li>
 *     <li>Atualizar transações existentes, incluindo alteração de conta ou categoria.</li>
 *     <li>Soft delete de transações, respeitando o campo {@code removedAt} para manter histórico.</li>
 *      <li>Validação de limites orçamentais e disparo de notificações de alerta/excesso.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final AuthClient authClient;
    private final TransactionMapper transactionMapper;
    private final AccountMapper accountMapper;
    private final CategoryMapper categoryMapper;
    private final NotificationService notificationService;

    /**
     * Retorna uma página de transações de um utilizador, com filtros opcionais.
     *
     * @param userId     ID do utilizador dono das transações.
     * @param accountId  ID da conta (opcional) para filtrar as transações.
     * @param categoryId ID da categoria (opcional) para filtrar as transações.
     * @param startDate  Data inicial (opcional) do intervalo de transações.
     * @param endDate    Data final (opcional) do intervalo de transações.
     * @param pageable   Objeto de paginação e ordenação.
     * @return Página de {@link TransactionDto} contendo as transações filtradas.
     */
    public Page<TransactionDto> getTransactions(UUID userId, UUID accountId, UUID categoryId,
                                                LocalDate startDate, LocalDate endDate) {

        UserInfoDto userInfo = authClient.getUserById(userId);

        PageRequest pageable = PageRequest.of(0, 20, Sort.by("transactionDate").descending());

        Page<Transaction> transactions = transactionRepository.findTransactionsByFilters(
                userId, accountId, categoryId, startDate, endDate, pageable);

        return transactions.map(trans -> transactionMapper.toTransactionDto(
                trans,
                userInfo,
                accountMapper.toAccountDto(trans.getAccount(), userInfo),
                categoryMapper.toCategoryDto(trans.getCategory(), userInfo)
        ));
    }

    /**
     * Retorna uma transação específica pelo seu ID.
     *
     * @param userId        ID do utilizador dono da transação.
     * @param transactionId ID da transação a ser retornada.
     * @return {@link TransactionDto} da transação encontrada.
     * @throws TransactionNotFoundException caso a transação não exista.
     */
    public TransactionDto getTransactionById(UUID userId, UUID transactionId) {
        UserInfoDto userInfo = authClient.getUserById(userId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found!"));

        return transactionMapper.toTransactionDto(transaction, userInfo,
                accountMapper.toAccountDto(transaction.getAccount(), userInfo),
                categoryMapper.toCategoryDto(transaction.getCategory(), userInfo));
    }

    /**
     * Cria uma nova transação para um utilizador.
     * <p>
     * Valida se a conta e categoria pertencem ao utilizador antes de persistir a transação.
     * Após a criação, atualiza o saldo da conta e verifica orçamentos.
     * </p>
     *
     * @param userId ID do utilizador que está criando a transação.
     * @param dto    {@link TransactionDto} contendo os dados da transação.
     * @return {@link TransactionDto} da transação criada.
     * @throws AccountNotFoundException  caso a conta não pertença ao utilizador ou não exista.
     * @throws CategoryNotFoundException caso a categoria não pertença ao utilizador ou não exista.
     */
    @Transactional
    public TransactionDto createTransaction(UUID userId, CreateTransactionDto dto) {
        UserInfoDto userInfo = authClient.getUserById(userId);

        Account account = accountRepository.findByIdAndUserId(dto.getAccountId(), userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        Category category = categoryRepository.findByIdAndUserId(dto.getCategoryId(), userId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found!"));

        Transaction transaction = Transaction.builder()
                .userId(userId)
                .account(account)
                .category(category)
                .amount(dto.getAmount())
                .transactionDate(dto.getTransactionDate())
                .description(dto.getDescription())
                .isRecurring(dto.getIsRecurring().toBoolean())
                .build();

        adjustAccountBalance(account, category, transaction.getAmount(), true);

        Transaction savedTransaction = transactionRepository.save(transaction);

        checkBudgetForAccount(account, userId);
        checkBudgetForCategory(category, userId);

        return transactionMapper.toTransactionDto(
                savedTransaction,
                userInfo,
                accountMapper.toAccountDto(account, userInfo),
                categoryMapper.toCategoryDto(category, userInfo)
        );
    }

    /**
     * Atualiza uma transação existente de um utilizador.
     * <p>
     * Permite alterar conta, categoria, valor, descrição, data e recorrência.
     * Recalcula o saldo da conta conforme as alterações.
     * </p>
     *
     * @param userId        ID do utilizador dono da transação.
     * @param transactionId ID da transação a ser atualizada.
     * @param dto           {@link UpdateTransactionDto} contendo os novos dados.
     * @return {@link TransactionDto} da transação atualizada.
     * @throws TransactionNotFoundException caso a transação não exista.
     * @throws AccountNotFoundException     caso a conta informada não exista ou não pertença ao utilizador.
     * @throws CategoryNotFoundException    caso a categoria informada não exista ou não pertença ao utilizador.
     */
    @Transactional
    public TransactionDto updateTransaction(UUID userId, UUID transactionId, UpdateTransactionDto dto) {
        UserInfoDto userInfo = authClient.getUserById(userId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found!"));

        if (transaction.getCategory().getType() == CategoryType.Income) {
            accountRepository.decreaseBalance(transaction.getAccount().getId(), userId, transaction.getAmount());
        } else if (transaction.getCategory().getType() == CategoryType.Expense) {
            accountRepository.increaseBalance(transaction.getAccount().getId(), userId, transaction.getAmount());
        }

        if(dto.getAccountId() != null) {
            Account account = accountRepository.findByIdAndUserId(dto.getAccountId(), userId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found!"));
            transaction.setAccount(account);
        }

        if(dto.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndUserId(dto.getCategoryId(), userId)
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found!"));
            transaction.setCategory(category);
        }

        if (dto.getAmount() != null) {
            transaction.setAmount(dto.getAmount());
        }
        if (dto.getTransactionDate() != null) {
            transaction.setTransactionDate(dto.getTransactionDate());
        }
        if (dto.getDescription() != null) {
            transaction.setDescription(dto.getDescription());
        }
        if (dto.getIsRecurring() != null) {
            transaction.setIsRecurring(dto.getIsRecurring().toBoolean());
        }

        adjustAccountBalance(transaction.getAccount(), transaction.getCategory(), transaction.getAmount(), true);

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toTransactionDto(
                updatedTransaction,
                userInfo,
                accountMapper.toAccountDto(updatedTransaction.getAccount(), userInfo),
                categoryMapper.toCategoryDto(updatedTransaction.getCategory(), userInfo)
        );
    }

    /**
     * Aplica soft delete a uma transação.
     * <p>
     * Marca a transação como removida definindo o campo {@code removedAt} com a data/hora atual.
     * Garante que apenas o utilizador dono da transação pode removê-la.
     * </p>
     *
     * @param transactionId ID da transação a ser removida.
     * @param userId        ID do utilizador dono da transação.
     * @throws RuntimeException caso a transação não exista.
     * @throws AccountNotFoundException caso a transação não pertença ao utilizador.
     */
    @Transactional
    public void deleteTransaction(UUID transactionId, UUID userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found!"));

        if(!transaction.getUserId().equals(userId)) {
            throw new AccountNotFoundException("Transaction not found!");
        }

        transactionRepository.softDeleteByIdAndUserId(transactionId, userId);
    }

    /**
     * Ajusta o saldo de uma conta com base no tipo da categoria e no valor da transação.
     * <p>
     * Caso a categoria seja do tipo {@code Income}, o saldo é aumentado no valor da transação.
     * Caso seja do tipo {@code Expense}, o saldo é reduzido no valor da transação.
     * O parâmetro {@code increase} controla se a operação é para aplicar ou reverter o impacto.
     * </p>
     *
     * @param account  Conta associada à transação.
     * @param category Categoria da transação (Income ou Expense).
     * @param amount   Valor da transação.
     * @param increase Indica se o saldo deve ser atualizado aplicando (true) ou revertendo (false) o impacto.
     */
    private void adjustAccountBalance(Account account, Category category, BigDecimal amount, boolean increase) {
        if (category.getType() == CategoryType.Income) {
            if (increase) {
                accountRepository.increaseBalance(account.getId(), account.getUserId(), amount);
            } else {
                accountRepository.decreaseBalance(account.getId(), account.getUserId(), amount);
            }
        } else if (category.getType() == CategoryType.Expense) {
            if (increase) {
                accountRepository.decreaseBalance(account.getId(), account.getUserId(), amount);
            } else {
                accountRepository.increaseBalance(account.getId(), account.getUserId(), amount);
            }
        }
    }

    /**
     * Verifica o orçamento mensal de uma conta após o registo de uma transação.
     * <p>
     * Caso o orçamento seja ultrapassado, envia uma notificação de tipo {@code BUDGET_EXCEEDED}.
     * Caso o valor remanescente seja menor ou igual a 10% do orçamento, envia uma notificação de tipo {@code BUDGET_WARNING}..
     * Se a conta não tiver orçamento definido, nada é feito.
     * </p>
     *
     * @param account Conta cuja transação foi registada.
     * @param userId  ID do utilizador dono da conta.
     */
    private void checkBudgetForAccount(Account account, UUID userId) {
        if (account.getMonthlyBudget() == null) return;

        BigDecimal spentThisMonth = transactionRepository.getSpentThisMonthForAccount(account.getId());
        BigDecimal remaining = account.getMonthlyBudget().subtract(spentThisMonth);
        BigDecimal warningThreshold = account.getMonthlyBudget().multiply(BigDecimal.valueOf(0.10));

        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            notificationService.sendNotification(
                    userId,
                    "BUDGET_EXCEEDED",
                    "You have exceeded your account " + account.getName() +
                            " budget in " + remaining.abs() + "€."
            );
        } else if (remaining.compareTo(warningThreshold) <= 0) {
            notificationService.sendNotification(
                    userId,
                    "BUDGET_WARNING",
                    "You are almost reaching your account " + account.getName() +
                            " budget. Remains " + remaining + "€."
            );
        }
    }

    /**
     * Verifica o orçamento mensal de uma categoria após o registo de uma transação.
     * <p>
     * Caso o orçamento seja ultrapassado, envia uma notificação de tipo {@code BUDGET_EXCEEDED}.
     * Caso o valor remanescente seja menor ou igual a 10% do orçamento, envia uma notificação de tipo {@code BUDGET_WARNING}.
     * Se a categoria não tiver orçamento definido, nada é feito.
     * </p>
     *
     * @param category Categoria cuja transação foi registada.
     * @param userId   ID do utilizador dono da categoria.
     */
    private void checkBudgetForCategory(Category category, UUID userId) {
        if (category.getMonthlyBudget() == null) return;

        BigDecimal spentThisMonth = transactionRepository.getSpentThisMonthForCategory(category.getId());
        BigDecimal remaining = category.getMonthlyBudget().subtract(spentThisMonth);
        BigDecimal warningThreshold = category.getMonthlyBudget().multiply(BigDecimal.valueOf(0.10));

        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            notificationService.sendNotification(
                    userId,
                    "BUDGET_EXCEEDED",
                    "You have exceeded the budget for category " + category.getName() +
                            " in " + remaining.abs() + "€."
            );
        } else if (remaining.compareTo(warningThreshold) <= 0) {
            notificationService.sendNotification(
                    userId,
                    "BUDGET_WARNING",
                    "You are almost at category " + category.getName() +
                            " budget limit. Remains " + remaining + "€."
            );
        }
    }


}

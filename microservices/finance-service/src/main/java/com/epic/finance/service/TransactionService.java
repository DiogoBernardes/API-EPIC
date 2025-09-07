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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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
     * Valida se a conta e a categoria pertencem ao utilizador antes de criar a transação.
     * </p>
     *
     * @param userId ID do utilizador que está criando a transação.
     * @param dto    {@link TransactionDto} contendo os dados da transação.
     * @return {@link TransactionDto} da transação criada.
     * @throws AccountNotFoundException  caso a conta não pertença ao utilizador ou não exista.
     * @throws CategoryNotFoundException caso a categoria não pertença ao utilizador ou não exista.
     */
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

        Transaction savedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toTransactionDto(
                savedTransaction,
                userInfo,
                accountMapper.toAccountDto(account, userInfo),
                categoryMapper.toCategoryDto(category, userInfo)
        );
    }

    /**
     * Atualiza uma transação existente.
     * <p>
     * Permite alterar conta, categoria, valor, descrição, data e recorrência da transação.
     * Valida se a conta e a categoria pertencem ao utilizador.
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
    public TransactionDto updateTransaction(UUID userId, UUID transactionId, UpdateTransactionDto dto) {
        UserInfoDto userInfo = authClient.getUserById(userId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found!"));

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
}

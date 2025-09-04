package com.epic.finance.service;

import com.epic.finance.client.AuthClient;
import com.epic.finance.dto.account.AccountDto;
import com.epic.finance.dto.account.CreateAccountDto;
import com.epic.finance.dto.account.UpdateAccountNameDto;
import com.epic.finance.dto.account.UpdateAccountStatusDto;
import com.epic.finance.entity.Account;
import com.epic.finance.exception.account.AccountNotFoundException;
import com.epic.finance.exception.account.ExistingAccountNameException;
import com.epic.finance.repository.account.AccountRepository;
import com.epic.shared.dto.UserInfoDto;
import com.epic.shared.enums.CommonStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Serviço responsável pela lógica de negócio relacionada às contas do utilizador.
 * <p>
 * Todas as operações consideram que o ID do utilizador é obtido via token JWT
 * e que os dados completos do utilizador podem ser recuperados através do AuthClient.
 * </p>
 *
 * Funcionalidades:
 * <ul>
 *     <li>Obter uma conta específica de um utilizador pelo nome.</li>
 *     <li>Listar todas as contas do utilizador, filtrando automaticamente contas eliminadas.</li>
 *     <li>Criar novas contas para o utilizador, garantindo que não haja duplicidade de nomes.</li>
 *     <li>Atualizar o nome ou estado de uma conta existente.</li>
 *     <li>Eliminar uma conta de forma lógica (soft delete), marcando o estado como INACTIVE e preenchendo o removedAt.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AuthClient authClient;

    /**
     * Obtém uma conta específica de um utilizador pelo nome.
     *
     * @param userId ID do utilizador logado, obtido via token JWT.
     * @param name Nome da conta a ser consultada.
     * @return AccountDto com detalhes da conta e dados do utilizador.
     * @throws AccountNotFoundException se não existir conta com esse nome para o utilizador
     *                                  ou se a conta estiver eliminada (soft delete).
     */
    public AccountDto getUserAccount(UUID userId, String name) {
        Account account = accountRepository.findByNameAndUserId(name, userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        UserInfoDto userInfo = authClient.getUserById(userId);

        return AccountDto.builder()
                .id(account.getId())
                .name(account.getName())
                .balance(account.getBalance())
                .status(account.getStatus())
                .user(userInfo)
                .build();
    }


    /**
     * Obtém todas as contas de um utilizador específico com um determinado estado.
     * Contas eliminadas (soft delete) são automaticamente ignoradas.
     *
     * @param userId ID do utilizador logado.
     * @param status Estado das contas a serem filtradas (ex: ACTIVE, INACTIVE).
     * @return Lista de AccountDto contendo todas as contas do utilizador que atendem ao estado.
     */
    public List<AccountDto> getUserAccounts(UUID userId, String status) {
        UserInfoDto userInfo = authClient.getUserById(userId);

        return accountRepository.findByStatusAndUserId(status, userId)
                .stream()
                .map(acc -> AccountDto.builder()
                        .id(acc.getId())
                        .name(acc.getName())
                        .balance(acc.getBalance())
                        .status(acc.getStatus())
                        .user(userInfo)
                        .build())
                .toList();
    }

    /**
     * Cria uma nova conta para um utilizador.
     *
     * @param dto DTO contendo os dados da nova conta (nome, saldo, etc.).
     * @param userId ID do utilizador que criará a conta.
     * @return Account criada e persistida no banco de dados.
     * @throws ExistingAccountNameException se já existir uma conta com o mesmo nome para o utilizador.
     */
    public Account createAccount(CreateAccountDto dto, UUID userId) {
        accountRepository.findByNameAndUserId(dto.getName(), userId)
                .ifPresent(acc -> {
                    throw new ExistingAccountNameException("There is already an account with that name.");
                });

        Account account = new Account();
        account.setUserId(userId);
        account.setName(dto.getName());
        account.setBalance(dto.getBalance() != null ? dto.getBalance() : BigDecimal.ZERO);
        account.setStatus(CommonStatus.Active.toString());

        return accountRepository.save(account);
    }

    /**
     * Atualiza o nome de uma conta existente.
     *
     * @param accountId ID da conta a ser atualizada.
     * @param userId ID do utilizador dono da conta.
     * @param dto DTO contendo o novo nome da conta.
     * @return Account atualizada.
     * @throws AccountNotFoundException se a conta não existir ou pertencer a outro utilizador.
     * @throws ExistingAccountNameException se já existir uma outra conta com o mesmo nome.
     */
    public Account updateAccountName(UUID accountId, UUID userId, UpdateAccountNameDto dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        if(!account.getUserId().equals(userId)) {
            throw new AccountNotFoundException("Account not found!");
        }

        accountRepository.findByNameAndUserId(dto.getName(), userId)
                .ifPresent(acc -> {
                    throw new ExistingAccountNameException("There is already an account with that name.");
                });

        account.setName(dto.getName());

        return accountRepository.save(account);
    }

    /**
     * Atualiza o estado (status) de uma conta existente.
     *
     * @param accountId ID da conta a ser atualizada.
     * @param userId ID do utilizador dono da conta.
     * @param dto DTO contendo o novo status da conta.
     * @return Account atualizada.
     * @throws AccountNotFoundException se a conta não existir ou pertencer a outro utilizador.
     */
    public Account updateAccountStatus(UUID accountId, UUID userId, UpdateAccountStatusDto dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        if(!account.getUserId().equals(userId)) {
            throw new AccountNotFoundException("Account not found!");
        }

        account.setStatus(dto.getStatus().toString());

        return accountRepository.save(account);
    }

    /**
     * Elimina uma conta de forma lógica (soft delete).
     * <p>
     * A operação atualiza o estado da conta para INACTIVE e preenche o campo removedAt,
     * garantindo que a conta não será mais retornada em consultas futuras.
     *
     * @param accountId ID da conta a ser eliminada.
     * @param userId ID do utilizador dono da conta.
     * @throws AccountNotFoundException se a conta não existir ou pertencer a outro utilizador.
     */
    @Transactional
    public void deleteAccount(UUID accountId, UUID userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        if(!account.getUserId().equals(userId)) {
            throw new AccountNotFoundException("Account not found!");
        }

        accountRepository.deactivateAndSoftDelete(accountId, userId, CommonStatus.Inactive.toString());
    }
}
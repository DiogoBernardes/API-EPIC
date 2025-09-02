package com.epic.finance.service;

import com.epic.finance.client.AuthClient;
import com.epic.finance.dto.account.AccountDto;
import com.epic.finance.dto.account.CreateAccountDto;
import com.epic.finance.entity.Account;
import com.epic.finance.exception.account.AccountNotFoundException;
import com.epic.finance.exception.account.ExistingAccountNameException;
import com.epic.finance.repository.account.AccountRepository;
import com.epic.shared.dto.UserInfoDto;
import com.epic.shared.enums.CommonStatus;
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
 *     <li>Listar todas as contas do utilizador.</li>
 *     <li>Criar novas contas para o utilizador, garantindo que não haja duplicidade de nomes.</li>
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
     * @throws AccountNotFoundException se não existir conta com esse nome para o utilizador.
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
     * Obtém todas as contas de um utilizador específico.
     *
     * @param userId ID do utilizador logado, obtido via token JWT.
     * @return Lista de AccountDto contendo todas as contas do utilizador.
     */
    public List<AccountDto> getUserAccounts(UUID userId) {
        UserInfoDto userInfo = authClient.getUserById(userId);

        return accountRepository.findByUserId(userId)
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
     * @param dto DTO contendo os dados da nova conta (incluindo userId).
     * @return Account criada e persistida no banco de dados.
     * @throws ExistingAccountNameException se já existir uma conta com o mesmo nome para o mesmo utilizador.
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
}
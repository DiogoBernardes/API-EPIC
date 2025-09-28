package com.epic.finance.controller;

import com.epic.finance.client.AuthClient;
import com.epic.finance.dto.account.*;
import com.epic.finance.entity.Account;
import com.epic.finance.service.AccountService;
import com.epic.shared.dto.UserInfoDto;
import com.epic.shared.enums.CommonStatus;
import com.epic.shared.security.JwtHelper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * Controller responsável pela gestão das contas do utilizador.
 * <p>
 * Todos os endpoints requerem autenticação via JWT.
 * O ID do utilizador é extraído automaticamente do token JWT enviado
 * no cabeçalho Authorization.
 * </p>
 *
 * Funcionalidades:
 * <ul>
 *     <li>Obter conta específica de um utilizador pelo nome.</li>
 *     <li>Listar todas as contas do utilizador logado, filtrando automaticamente contas eliminadas (soft delete).</li>
 *     <li>Criar uma nova conta para o utilizador logado.</li>
 *     <li>Atualizar nome ou estado de uma conta.</li>
 *     <li>Eliminar uma conta de forma lógica (soft delete), marcando o estado como INACTIVE.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@RestController
@RequestMapping("/finance/account")
@Tag(name = "Accounts", description = "Gestão das contas do Utilizador")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final JwtHelper jwtHelper;
    private final AuthClient authClient;

    /**
     * Obter uma conta específica de um utilizador pelo nome.
     *
     * @param authHeader Cabeçalho Authorization com o token JWT.
     * @param name Nome da conta.
     * @return AccountDto com informações da conta e do utilizador.
     * @throws AccountNotFoundException se a conta não existir ou estiver eliminada.
     */
    @GetMapping("/{name}")
    public ResponseEntity<AccountDto> getAccount(@RequestHeader("Authorization") String authHeader,
                                                 @PathVariable("name") String name) {
        UUID userId = jwtHelper.extractUserId(authHeader);
        AccountDto account = accountService.getUserAccount(userId, name);
        return ResponseEntity.ok(account);
    }


    /**
     * Obter todas as contas do utilizador logado, filtrando por estados.
     * Contas eliminadas (soft delete) não são retornadas.
     *
     * @param authHeader Cabeçalho Authorization com o token JWT.
     * @param status Status das contas a serem filtradas (default: Active).
     * @return Lista de AccountDto.
     */
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> getAccounts(@RequestHeader("Authorization") String authHeader,
                                                        @RequestParam(value = "status", defaultValue = "Active") CommonStatus status) {
        UUID userId = jwtHelper.extractUserId(authHeader);
        List<AccountDto> accounts = accountService.getUserAccounts(userId, status.toString());
        return ResponseEntity.ok(accounts);
    }

    /**
     * Criar uma nova conta para o utilizador logado.
     *
     * @param dto Dados da nova conta.
     * @param authHeader Cabeçalho Authorization com o token JWT.
     * @return AccountDto com a conta criada.
     * @throws ExistingAccountNameException se já existir uma conta com o mesmo nome para o utilizador.
     */
    @PostMapping("/create")
    public ResponseEntity<AccountDto> createAccount(@RequestBody CreateAccountDto dto,
                                                    @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtHelper.extractUserId(authHeader);

        Account account = accountService.createAccount(dto, userId);

        UserInfoDto userInfo = authClient.getUserById(userId);

        AccountDto accountDto = AccountDto.builder()
                .id(account.getId())
                .name(account.getName())
                .balance(account.getBalance())
                .status(account.getStatus())
                .user(userInfo)
                .monthlyBudget(account.getMonthlyBudget())
                .build();

        return ResponseEntity.ok(accountDto);
    }

    /**
     * Atualiza o nome de uma conta existente.
     *
     * @param accountId ID da conta.
     * @param dto Novo nome da conta.
     * @param authHeader Cabeçalho Authorization com o token JWT.
     * @return AccountDto atualizado.
     * @throws AccountNotFoundException se a conta não pertencer ao utilizador ou não existir.
     * @throws ExistingAccountNameException se já existir outra conta com o mesmo nome.
     */
    @PutMapping("/update/{accountId}")
    public ResponseEntity<AccountDto> updateAccountName(@PathVariable("accountId") UUID accountId,
                                                        @RequestBody UpdateAccountDto dto,
                                                        @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtHelper.extractUserId(authHeader);

        Account updatedAccount = accountService.updateAccount(accountId, userId, dto);

        UserInfoDto userInfo = authClient.getUserById(userId);

        AccountDto accountDto = AccountDto.builder()
                .id(updatedAccount.getId())
                .name(updatedAccount.getName())
                .balance(updatedAccount.getBalance())
                .status(updatedAccount.getStatus())
                .user(userInfo)
                .build();

        return ResponseEntity.ok(accountDto);
    }


    /**
     * Elimina uma conta de forma lógica (soft delete), marcando o estado como INACTIVE.
     *
     * @param accountId ID da conta a ser eliminada.
     * @param authHeader Cabeçalho Authorization com o token JWT.
     * @return ResponseEntity sem conteúdo (204).
     * @throws AccountNotFoundException se a conta não pertencer ao utilizador ou não existir.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") UUID accountId,
                                              @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtHelper.extractUserId(authHeader);

        accountService.deleteAccount(accountId, userId);

        return ResponseEntity.noContent().build();
    }
}

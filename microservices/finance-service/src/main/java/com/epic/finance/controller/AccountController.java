package com.epic.finance.controller;

import com.epic.finance.client.AuthClient;
import com.epic.finance.dto.account.AccountDto;
import com.epic.finance.dto.account.CreateAccountDto;
import com.epic.finance.entity.Account;
import com.epic.finance.service.AccountService;
import com.epic.shared.dto.UserInfoDto;
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
 *     <li>Listar todas as contas do utilizador logado.</li>
 *     <li>Criar uma nova conta para o utilizador logado.</li>
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
     */
    @GetMapping("/{name}")
    public ResponseEntity<AccountDto> getAccount(@RequestHeader("Authorization") String authHeader,
                                                 @PathVariable("name") String name) {
        UUID userId = jwtHelper.extractUserId(authHeader);
        AccountDto account = accountService.getUserAccount(userId, name);
        return ResponseEntity.ok(account);
    }

    /**
     * Obter todas as contas do utilizador logado.
     *
     * @param authHeader Cabeçalho Authorization com o token JWT.
     * @return Lista de AccountDto.
     */
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> getAccounts(@RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtHelper.extractUserId(authHeader);
        List<AccountDto> accounts = accountService.getUserAccounts(userId);
        return ResponseEntity.ok(accounts);
    }

    /**
     * Criar uma nova conta para o utilizador logado.
     *
     * @param dto Dados da nova conta.
     * @param authHeader Cabeçalho Authorization com o token JWT.
     * @return AccountDto com a conta criada.
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
                .build();

        return ResponseEntity.ok(accountDto);
    }
}

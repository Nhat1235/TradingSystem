package org.aquariux.tradingsystem.wallet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.aquariux.tradingsystem.common.ApiResponse;
import org.aquariux.tradingsystem.common.Constants;
import org.aquariux.tradingsystem.wallet.models.response.WalletBalanceResponse;
import org.aquariux.tradingsystem.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_BASE_PATH + "/wallets")
@Tag(name = "Wallet", description = "Wallet balance endpoints")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/balance")
    @Operation(summary = "Get wallet balances", description = "Retrieve user's crypto wallet balances")
    public ResponseEntity<ApiResponse<WalletBalanceResponse>> getWalletBalance(
            @RequestParam("accountId") long accountId) {
        WalletBalanceResponse response = walletService.getWalletBalance(accountId);
        return ResponseEntity.ok(ApiResponse.success("Wallet balance retrieved", response));
    }
}

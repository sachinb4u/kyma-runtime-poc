package com.sap.banking.positivepay.controller.beans;

import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.http.HttpStatus;

import java.util.List;

public class AccountListResponse {
    @JsonAlias({"d"})
    private InternalAccountResponse d;
    private HttpStatus status;

    public InternalAccountResponse getD() {
        return d;
    }

    public void setD(InternalAccountResponse d) {
        this.d = d;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public class InternalAccountResponse{
        @JsonAlias({"results"})
        private List<Account> results;

        public List<Account> getResults() {
            return results;
        }

        public void setResults(List<Account> results) {
            this.results = results;
        }
    }
}

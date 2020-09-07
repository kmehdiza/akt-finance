package az.ingress.akt.service;

import az.ingress.akt.domain.Loan;

public interface LoanService {

    Loan checkByIdAndReturnLoan(Long applicationId);

}

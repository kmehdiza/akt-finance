package az.ingress.akt.service;

import az.ingress.akt.domain.Loan;

public interface LoanService {

    Loan checkLoanByIdAndStep(Long applicationId);

    Loan checkLoanById(Long applicationId);

}

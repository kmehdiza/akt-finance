package az.ingress.akt.dto;

import az.ingress.akt.domain.enums.ApplicationStep;

public class LoanDto {

    private Long id;

    private ApplicationStep applicationStep;

    private String agentUsername;

    private PersonDto debtor;
}

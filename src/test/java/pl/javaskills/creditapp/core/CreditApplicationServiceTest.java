package pl.javaskills.creditapp.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.javaskills.creditapp.core.model.CreditApplication;
import pl.javaskills.creditapp.core.model.CreditApplicationTestFactory;
import pl.javaskills.creditapp.core.model.PurposeOfLoanType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class CreditApplicationServiceTest {

    @InjectMocks
    private CreditApplicationService cut;

    @Mock
    private PersonScoringCalculator calculatorMock;

    @Mock
    private CreditRatingCalculator creditRatingCalculatorMock;

    @Test
    @DisplayName("should return NEGATIVE_SCORING decision, when scoring is < 300")
    public void test1() {
        //given
        CreditApplication creditApplication = CreditApplicationTestFactory.create();
        BDDMockito.given(calculatorMock.calculate(eq(creditApplication.getPerson())))
                .willReturn(100);

        //when
        CreditApplicationDecision decision = cut.getDecision(creditApplication);
        //then
        assertEquals(DecisionType.NEGATIVE_SCORING, decision.getType());

    }

    @Test
    @DisplayName("should return CONTACT_REQUIRED decision, when scoring is <= 400")
    public void test2() {
        //given
        CreditApplication creditApplication = CreditApplicationTestFactory.create();
        BDDMockito.given(calculatorMock.calculate(eq(creditApplication.getPerson())))
                .willReturn(350);

        //when
        CreditApplicationDecision decision = cut.getDecision(creditApplication);
        //then
        assertEquals(DecisionType.CONTACT_REQUIRED, decision.getType());

    }

    @Test
    @DisplayName("should return NEGATIVE_RATING decision, when scoring is > 400 and credit rating > expected loan amount")
    public void test3() {
        //given
        CreditApplication creditApplication = CreditApplicationTestFactory.create(190000.00);
        BDDMockito.given(calculatorMock.calculate(eq(creditApplication.getPerson())))
                .willReturn(450);

        BDDMockito.given(creditRatingCalculatorMock.calculate(eq(creditApplication))).
                willReturn(189000.00);

        //when
        CreditApplicationDecision decision = cut.getDecision(creditApplication);
        //then
        assertEquals(DecisionType.NEGATIVE_RATING, decision.getType());

    }

    @Test
    @DisplayName("should return POSITIVE decision, when scoring is > 400 and credit rating <= expected loan amount")
    public void test4() {
        //given
        CreditApplication creditApplication = CreditApplicationTestFactory.create(50000.00);
        BDDMockito.given(calculatorMock.calculate(eq(creditApplication.getPerson())))
                .willReturn(450);
        BDDMockito.given(creditRatingCalculatorMock.calculate(eq(creditApplication))).
                willReturn(51000.00);
        //when
        CreditApplicationDecision decision = cut.getDecision(creditApplication);
        //then
        assertEquals(DecisionType.POSITIVE, decision.getType());

    }

}
package co.com.crediya.model.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

import co.com.crediya.model.valueobject.exception.InvalidBaseSalaryException;

public class BaseSalary {

    private final BigDecimal value;
    
    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal(150000000);

    public BaseSalary(BigDecimal value){
        if (value == null) {
            throw new InvalidBaseSalaryException();
        }

        if (value.compareTo(MIN_SALARY) < 0) {
            throw new InvalidBaseSalaryException("cannot be less than " + MIN_SALARY);
        }

        if (value.compareTo(MAX_SALARY) > 0) {
            throw new InvalidBaseSalaryException("cannot exceed " + MAX_SALARY);
        }
        
        this.value = value;
    }

    public BigDecimal getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseSalary that = (BaseSalary) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

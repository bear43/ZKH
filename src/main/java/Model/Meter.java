package Model;

import Util.MeterType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Meter
{
    @Id
    @GeneratedValue
    private Long id;

    private MeterType type;

    private double value;

    private double cost;

    @ManyToOne
    private User user;

    @ManyToOne
    private Payment payment;

    protected Meter()
    {

    }

    public Meter(MeterType type, double value, double cost, User user)
    {
        this.type = type;
        this.value = value;
        this.cost = cost;
        this.user = user;
    }

    public Meter(Meter meter)
    {
        this.type = meter.type;
        this.cost = meter.cost;
        this.value = meter.value;
        this.payment = meter.payment;
        this.user = meter.user;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public MeterType getType()
    {
        return type;
    }

    public void setType(MeterType type)
    {
        this.type = type;
    }

    public double getValue()
    {
        return value;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

    public double getCost()
    {
        return cost;
    }

    public void setCost(double cost)
    {
        this.cost = cost;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public double getTotalCost()
    {
        return value*cost;
    }

    public void copy(Meter meter)
    {
        this.value = meter.value;
        this.cost = meter.cost;
        this.type = meter.type;
    }

    public Payment getPayment()
    {
        return payment;
    }

    public void setPayment(Payment payment)
    {
        this.payment = payment;
    }

    @Override
    public String toString()
    {
        return String.format("Вид: %s, Значение: %f, Стоимость единицы: %f", type.toString(), value, cost);
    }
}

package Model;

import Util.MeterFactory;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Payment
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "payment",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Meter> meterSet = new HashSet<>();

    private Date date;

    @ManyToOne
    private User user;

    protected Payment()
    {

    }

    public Payment(Set<Meter> meterSet, Date date, User user)
    {
        this.date = date;
        for(Meter meter : meterSet)
        {
            meter = MeterFactory.createCopiedInstance(meter);
            meter.setUser(null);
            meter.setPayment(this);
            this.meterSet.add(meter);
        }
        this.user = user;
    }

    public Payment(Set<Meter> meterSet, User user)
    {
        this(meterSet, Date.valueOf(LocalDate.now()), user);
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Set<Meter> getMeterSet()
    {
        return meterSet;
    }

    public void setMeterSet(Set<Meter> meterSet)
    {
        this.meterSet = meterSet;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public double getTotal()
    {
        double total = 0.0;
        for(Meter meter : meterSet)
            total += meter.getTotalCost();
        return total;
    }

    @Override
    public String toString()
    {
        return String.format("ID: %d, Счетчик: %s, Дата: %s, Итог: %f", id, meterSet.toString(), date.toString(), getTotal());
    }
}

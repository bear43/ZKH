package Model;

import Repository.MeterRepository;
import Repository.UserRepository;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usr")
public class User
{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String name;

    private String password;

    private boolean active;

    private int authority;

    private String firstname;

    private String secondname;

    private String patronymic;

    private String address;

    private String number;

    @OneToMany(mappedBy = "user")
    private Set<Meter> meterSet;

    protected User()
    {
    }

    public User(String name, String password, boolean active, int authority,
                String firstname, String secondname, String patronymic, String address, String number)
    {
        this(name, password, active, authority);
        this.firstname = firstname;
        this.secondname = secondname;
        this.patronymic = patronymic;
        this.address = address;
        this.number = number;
        meterSet = new HashSet<>();
        for(MeterType type : MeterType.values())
        {
            meterSet.add(new Meter(type, 0.0, 0.0, this));
        }
    }

    public User(String name, String password, boolean active, int authority,
                String firstname, String secondname, String patronymic,
                String address, String number, UserRepository userRepository, MeterRepository meterRepository)
    {
        this(name, password, active, authority);
        this.firstname = firstname;
        this.secondname = secondname;
        this.patronymic = patronymic;
        this.address = address;
        this.number = number;
        meterSet = new HashSet<>();
        userRepository.saveAndFlush(this);
        Meter meter;
        for(MeterType type : MeterType.values())
        {
            meter = new Meter(type, 0.0, 0.0, this);
            meterSet.add(meter);
            meterRepository.save(meter);
        }
    }

    public User(User user)
    {
        this(user.name, user.password, user.active, user.authority,
                user.firstname, user.secondname, user.patronymic, user.address, user.number);
    }

    public User(User user, UserRepository userRepository, MeterRepository meterRepository)
    {
        this(user.name, user.password, user.active, user.authority,
                user.firstname, user.secondname, user.patronymic,
                user.address, user.number, userRepository, meterRepository);
    }

    public User(String name, String password, boolean active, int authority)
    {
        this.name = name;
        this.password = password;
        this.active = active;
        this.authority = authority;
    }

    public User(String name, String password, boolean active)
    {
        this(name, password, active, 0);
    }

    public User(String name, String password)
    {
        this(name, password, true);
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public int getAuthority()
    {
        return authority;
    }

    public void setAuthority(int authority)
    {
        this.authority = authority;
    }

    public String getFirstname()
    {
        return firstname;
    }

    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }

    public String getSecondname()
    {
        return secondname;
    }

    public void setSecondname(String secondname)
    {
        this.secondname = secondname;
    }

    public String getPatronymic()
    {
        return patronymic;
    }

    public void setPatronymic(String patronymic)
    {
        this.patronymic = patronymic;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public Set<Meter> getMeterSet()
    {
        return meterSet;
    }

    public void setMeterSet(Set<Meter> meterSet)
    {
        this.meterSet = meterSet;
    }

    public Meter getMeterByType(MeterType type) throws Exception
    {
        if(meterSet == null) throw new Exception("Attempt to search meter but meter set is null!");
        for(Meter m : meterSet)
        {
            if(m.getType() == type)
                return m;
        }
        throw new Exception("No such type " + type.toString() + "!");
    }
}

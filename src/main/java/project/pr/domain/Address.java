package project.pr.domain;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address implements Serializable {

    private String street;
    private String city;

    @Override
    public int hashCode() {
        int result = Objects.hash(getCity(), getStreet());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(obj == null || getClass() != obj.getClass()){
            return false;
        }
        Address address = (Address) obj;
        return Objects.equals(getCity() , address.getCity()) && Objects.equals(getStreet() , address.getCity());
    }
}

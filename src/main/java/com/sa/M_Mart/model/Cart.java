package com.sa.M_Mart.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id",updatable = false,nullable = false)
    private Long id;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private AppUser user;

    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CartItem>items=new ArrayList<>();

    public void addItem(CartItem item){
        this.items.add(item); //add to list
        item.setCart(this);
    }

    public void removeItem(CartItem item){
        this.items.remove(item);
        item.setCart(null);
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj) return true;
        if (!(obj instanceof Cart)) return false;
        Cart other =(Cart) obj;
        return id !=null && id.equals(other.getId());

    }

    public int hashCode(){
        return getClass().hashCode();
    }
}

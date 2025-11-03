package com.sa.M_Mart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id",updatable = false,nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "cart_id",nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity=1;

    @Override
    public boolean equals(Object obj){
        if (this==obj) return true;
        if(!(obj instanceof CartItem)) return false;

        CartItem other =(CartItem) obj;
        return id !=null && id.equals(other.getId());
        // return id != null && id.equals(((CartItem) o).getId());
    }

    @Override
    public  int hashCode(){
        return getClass().hashCode();
    }
}

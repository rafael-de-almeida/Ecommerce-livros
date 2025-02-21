package com.projeto.Ecommerce.model;
import jakarta.persistence.*;

import lombok.*;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "cliente")

public class Cliente {
    @Id
    private Integer id;
    private String Nome;
}

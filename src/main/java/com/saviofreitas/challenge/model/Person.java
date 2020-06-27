package com.saviofreitas.challenge.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "pessoas", uniqueConstraints = @UniqueConstraint(columnNames = {"id", "email"}))
public class Person implements Serializable {

	private static final long serialVersionUID = 8178539457997826699L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Campo obrigatório")
	@Column(name = "nome", nullable = false)
	private String firstName;
	
	@NotBlank(message = "Campo obrigatório")
	@Column(name = "sobrenome", nullable = false)
	private String lastName;

	@NotBlank(message = "Campo obrigatório")
	@Email(message = "O campo email não parece um email válido.")
	@Column(name = "email", nullable = false, unique = true)
	private String email;
	
	@ManyToOne(optional = false, cascade = CascadeType.MERGE)
	@JoinColumn(name = "setor_id")
	private Departament departament;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Departament getDepartament() {
		return departament;
	}

	public void setDepartament(Departament departament) {
		this.departament = departament;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Person)) {
			return false;
		}
		Person other = (Person) obj;
		if (email == null) {
			if (other.email != null) {
				return false;
			}
		} else if (!email.equals(other.email)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{'id': ");
		builder.append(id);
		builder.append(", 'firstName': '");
		builder.append(firstName);
		builder.append("', 'lastName': '");
		builder.append(lastName);
		builder.append("', 'email': '");
		builder.append(email);
		builder.append("', 'departament': ");
		builder.append(departament);
		builder.append("}");
		return builder.toString();
	}
	
	

}

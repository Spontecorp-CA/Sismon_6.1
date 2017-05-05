/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sismon.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jgcastillo
 */
@Entity
@Table(name = "paridad")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Paridad.findAll", query = "SELECT p FROM Paridad p"),
    @NamedQuery(name = "Paridad.findById", query = "SELECT p FROM Paridad p WHERE p.id = :id"),
    @NamedQuery(name = "Paridad.findByValor", query = "SELECT p FROM Paridad p WHERE p.valor = :valor"),
    @NamedQuery(name = "Paridad.findByFechaIn", query = "SELECT p FROM Paridad p WHERE p.fechaIn = :fechaIn"),
    @NamedQuery(name = "Paridad.findByFechaOut", query = "SELECT p FROM Paridad p WHERE p.fechaOut = :fechaOut"),
    @NamedQuery(name = "Paridad.findByStatus", query = "SELECT p FROM Paridad p WHERE p.status = :status")})
public class Paridad implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private Double valor;
    @Column(name = "fecha_in")
    @Temporal(TemporalType.DATE)
    private Date fechaIn;
    @Column(name = "fecha_out")
    @Temporal(TemporalType.DATE)
    private Date fechaOut;
    @Column(name = "status")
    private Integer status;

    public Paridad() {
    }

    public Paridad(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Date getFechaIn() {
        return fechaIn;
    }

    public void setFechaIn(Date fechaIn) {
        this.fechaIn = fechaIn;
    }

    public Date getFechaOut() {
        return fechaOut;
    }

    public void setFechaOut(Date fechaOut) {
        this.fechaOut = fechaOut;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Paridad)) {
            return false;
        }
        Paridad other = (Paridad) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.Paridad[ id=" + id + " ]";
    }
    
}

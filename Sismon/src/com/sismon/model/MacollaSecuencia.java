/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sismon.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jgcastillo
 */
@Entity
@Table(name = "macolla_secuencia")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MacollaSecuencia.findAll", query = "SELECT m FROM MacollaSecuencia m"),
    @NamedQuery(name = "MacollaSecuencia.findById", query = "SELECT m FROM MacollaSecuencia m WHERE m.id = :id"),
    @NamedQuery(name = "MacollaSecuencia.findBySecuencia", query = "SELECT m FROM MacollaSecuencia m WHERE m.secuencia = :secuencia")})
public class MacollaSecuencia implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "secuencia")
    private Integer secuencia;
    @JoinColumn(name = "escenario_id", referencedColumnName = "id")
    @ManyToOne
    private Escenario escenarioId;
    @JoinColumn(name = "macolla_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Macolla macollaId;

    public MacollaSecuencia() {
    }

    public MacollaSecuencia(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(Integer secuencia) {
        this.secuencia = secuencia;
    }

    public Escenario getEscenarioId() {
        return escenarioId;
    }

    public void setEscenarioId(Escenario escenarioId) {
        this.escenarioId = escenarioId;
    }

    public Macolla getMacollaId() {
        return macollaId;
    }

    public void setMacollaId(Macolla macollaId) {
        this.macollaId = macollaId;
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
        if (!(object instanceof MacollaSecuencia)) {
            return false;
        }
        MacollaSecuencia other = (MacollaSecuencia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.MacollaSecuencia[ id=" + id + " ]";
    }
    
}

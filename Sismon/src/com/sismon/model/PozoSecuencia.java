/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sismon.model;

import java.io.Serializable;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author jgcastillo
 */
@Entity
@Table(name = "pozo_secuencia")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PozoSecuencia.findAll", query = "SELECT p FROM PozoSecuencia p"),
    @NamedQuery(name = "PozoSecuencia.findById", query = "SELECT p FROM PozoSecuencia p WHERE p.id = :id"),
    @NamedQuery(name = "PozoSecuencia.findBySecuencia", query = "SELECT p FROM PozoSecuencia p WHERE p.secuencia = :secuencia"),
    @NamedQuery(name = "PozoSecuencia.findByFase", query = "SELECT p FROM PozoSecuencia p WHERE p.fase = :fase")})
public class PozoSecuencia implements Serializable, Comparable<PozoSecuencia> {

    @OneToMany(mappedBy = "pozoSecuenciaInId")
    private Collection<TaladroAsignado> taladroAsignadoCollection;
    @OneToMany(mappedBy = "pozoSecuenciaOutId")
    private Collection<TaladroAsignado> taladroAsignadoCollection1;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "secuencia")
    private Integer secuencia;
    @Column(name = "fase")
    private String fase;
    @JoinColumn(name = "escenario_id", referencedColumnName = "id")
    @ManyToOne
    private Escenario escenarioId;
    @JoinColumn(name = "fila_id", referencedColumnName = "id")
    @ManyToOne
    private Fila filaId;
    @JoinColumn(name = "pozo_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Pozo pozoId;

    public PozoSecuencia() {
    }

    public PozoSecuencia(Long id) {
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

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public Escenario getEscenarioId() {
        return escenarioId;
    }

    public void setEscenarioId(Escenario escenarioId) {
        this.escenarioId = escenarioId;
    }

    public Fila getFilaId() {
        return filaId;
    }

    public void setFilaId(Fila filaId) {
        this.filaId = filaId;
    }

    public Pozo getPozoId() {
        return pozoId;
    }

    public void setPozoId(Pozo pozoId) {
        this.pozoId = pozoId;
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
        if (!(object instanceof PozoSecuencia)) {
            return false;
        }
        PozoSecuencia other = (PozoSecuencia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.PozoSecuencia[ id=" + id + " ]";
    }
    
    @Override
    public int compareTo(PozoSecuencia other) {
        return this.secuencia.compareTo(other.secuencia);
    }

    @XmlTransient
    public Collection<TaladroAsignado> getTaladroAsignadoCollection() {
        return taladroAsignadoCollection;
    }

    public void setTaladroAsignadoCollection(Collection<TaladroAsignado> taladroAsignadoCollection) {
        this.taladroAsignadoCollection = taladroAsignadoCollection;
    }

    @XmlTransient
    public Collection<TaladroAsignado> getTaladroAsignadoCollection1() {
        return taladroAsignadoCollection1;
    }

    public void setTaladroAsignadoCollection1(Collection<TaladroAsignado> taladroAsignadoCollection1) {
        this.taladroAsignadoCollection1 = taladroAsignadoCollection1;
    }
}

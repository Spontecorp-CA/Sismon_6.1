/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sismon.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author jgcastillo
 */
@Entity
@Table(name = "taladro")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Taladro.findAll", query = "SELECT t FROM Taladro t"),
    @NamedQuery(name = "Taladro.findById", query = "SELECT t FROM Taladro t WHERE t.id = :id"),
    @NamedQuery(name = "Taladro.findByNombre", query = "SELECT t FROM Taladro t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "Taladro.findByFechaInicial", query = "SELECT t FROM Taladro t WHERE t.fechaInicial = :fechaInicial")})
public class Taladro implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "fecha_inicial")
    @Temporal(TemporalType.DATE)
    private Date fechaInicial;
    @OneToMany(mappedBy = "taladroId")
    private Collection<Perforacion> perforacionCollection;
    @JoinColumn(name = "escenario_id", referencedColumnName = "id")
    @ManyToOne
    private Escenario escenarioId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "taladroId")
    private Collection<FilaHasTaladro> filaHasTaladroCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "taladroId")
    private Collection<TaladroHasFase> taladroHasFaseCollection;
    @OneToMany(mappedBy = "taladroId")
    private Collection<TaladroStatus> taladroStatusCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "taladroId")
    private Collection<TaladroMant> taladroMantCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "taladroOriginal")
    private Collection<TaladroSustituto> taladroSustitutoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "taladroId")
    private Collection<TaladroAsignado> taladroAsignadoCollection;

    public Taladro() {
    }

    public Taladro(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    @XmlTransient
    public Collection<Perforacion> getPerforacionCollection() {
        return perforacionCollection;
    }

    public void setPerforacionCollection(Collection<Perforacion> perforacionCollection) {
        this.perforacionCollection = perforacionCollection;
    }

    public Escenario getEscenarioId() {
        return escenarioId;
    }

    public void setEscenarioId(Escenario escenarioId) {
        this.escenarioId = escenarioId;
    }

    @XmlTransient
    public Collection<FilaHasTaladro> getFilaHasTaladroCollection() {
        return filaHasTaladroCollection;
    }

    public void setFilaHasTaladroCollection(Collection<FilaHasTaladro> filaHasTaladroCollection) {
        this.filaHasTaladroCollection = filaHasTaladroCollection;
    }

    @XmlTransient
    public Collection<TaladroHasFase> getTaladroHasFaseCollection() {
        return taladroHasFaseCollection;
    }

    public void setTaladroHasFaseCollection(Collection<TaladroHasFase> taladroHasFaseCollection) {
        this.taladroHasFaseCollection = taladroHasFaseCollection;
    }

    @XmlTransient
    public Collection<TaladroStatus> getTaladroStatusCollection() {
        return taladroStatusCollection;
    }

    public void setTaladroStatusCollection(Collection<TaladroStatus> taladroStatusCollection) {
        this.taladroStatusCollection = taladroStatusCollection;
    }

    @XmlTransient
    public Collection<TaladroMant> getTaladroMantCollection() {
        return taladroMantCollection;
    }

    public void setTaladroMantCollection(Collection<TaladroMant> taladroMantCollection) {
        this.taladroMantCollection = taladroMantCollection;
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
        if (!(object instanceof Taladro)) {
            return false;
        }
        Taladro other = (Taladro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @XmlTransient
    public Collection<TaladroSustituto> getTaladroSustitutoCollection() {
        return taladroSustitutoCollection;
    }

    public void setTaladroSustitutoCollection(Collection<TaladroSustituto> taladroSustitutoCollection) {
        this.taladroSustitutoCollection = taladroSustitutoCollection;
    }

    @XmlTransient
    public Collection<TaladroAsignado> getTaladroAsignadoCollection() {
        return taladroAsignadoCollection;
    }

    public void setTaladroAsignadoCollection(Collection<TaladroAsignado> taladroAsignadoCollection) {
        this.taladroAsignadoCollection = taladroAsignadoCollection;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sismon.model;

import java.io.Serializable;
import java.util.Collection;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author jgcastillo
 */
@Entity
@Table(name = "fila")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Fila.findAll", query = "SELECT f FROM Fila f"),
    @NamedQuery(name = "Fila.findById", query = "SELECT f FROM Fila f WHERE f.id = :id"),
    @NamedQuery(name = "Fila.findByNombre", query = "SELECT f FROM Fila f WHERE f.nombre = :nombre")})
public class Fila implements Serializable {

    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "nombre")
    private String nombre;
    @OneToMany(mappedBy = "filaId")
    private Collection<Perforacion> perforacionCollection;
    @OneToMany(mappedBy = "filaId")
    private Collection<Pozo> pozoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "filaId")
    private Collection<FilaHasTaladro> filaHasTaladroCollection;
    @OneToMany(mappedBy = "filaId")
    private Collection<PozoSecuencia> pozoSecuenciaCollection;
    @JoinColumn(name = "macolla_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Macolla macollaId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "filaId")
    private Collection<TaladroAsignado> taladroAsignadoCollection;

    public Fila() {
    }

    public Fila(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlTransient
    public Collection<Perforacion> getPerforacionCollection() {
        return perforacionCollection;
    }

    public void setPerforacionCollection(Collection<Perforacion> perforacionCollection) {
        this.perforacionCollection = perforacionCollection;
    }

    @XmlTransient
    public Collection<Pozo> getPozoCollection() {
        return pozoCollection;
    }

    public void setPozoCollection(Collection<Pozo> pozoCollection) {
        this.pozoCollection = pozoCollection;
    }

    @XmlTransient
    public Collection<FilaHasTaladro> getFilaHasTaladroCollection() {
        return filaHasTaladroCollection;
    }

    public void setFilaHasTaladroCollection(Collection<FilaHasTaladro> filaHasTaladroCollection) {
        this.filaHasTaladroCollection = filaHasTaladroCollection;
    }

    @XmlTransient
    public Collection<PozoSecuencia> getPozoSecuenciaCollection() {
        return pozoSecuenciaCollection;
    }

    public void setPozoSecuenciaCollection(Collection<PozoSecuencia> pozoSecuenciaCollection) {
        this.pozoSecuenciaCollection = pozoSecuenciaCollection;
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
        if (!(object instanceof Fila)) {
            return false;
        }
        Fila other = (Fila) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        if (this.macollaId.equals(other.macollaId)) {
            if (!this.nombre.equalsIgnoreCase(other.nombre)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @XmlTransient
    public Collection<TaladroAsignado> getTaladroAsignadoCollection() {
        return taladroAsignadoCollection;
    }

    public void setTaladroAsignadoCollection(Collection<TaladroAsignado> taladroAsignadoCollection) {
        this.taladroAsignadoCollection = taladroAsignadoCollection;
    }
    
}

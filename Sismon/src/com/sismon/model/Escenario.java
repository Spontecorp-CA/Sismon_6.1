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
@Table(name = "escenario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Escenario.findAll", query = "SELECT e FROM Escenario e"),
    @NamedQuery(name = "Escenario.findById", query = "SELECT e FROM Escenario e WHERE e.id = :id"),
    @NamedQuery(name = "Escenario.findByNombre", query = "SELECT e FROM Escenario e WHERE e.nombre = :nombre"),
    @NamedQuery(name = "Escenario.findByFecha", query = "SELECT e FROM Escenario e WHERE e.fecha = :fecha"),
    @NamedQuery(name = "Escenario.findByComentario", query = "SELECT e FROM Escenario e WHERE e.comentario = :comentario"),
    @NamedQuery(name = "Escenario.findByStatus", query = "SELECT e FROM Escenario e WHERE e.status = :status")})
public class Escenario implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "comentario")
    private String comentario;
    @Column(name = "fecha_cierre")
    @Temporal(TemporalType.DATE)
    private Date fechaCierre;
    @Column(name = "status")
    private Integer status;
    @Column(name = "archivo")
    private String archivo;
    @Column(name = "tipo")
    private Integer tipo;
    @OneToMany(mappedBy = "escenarioId")
    private Collection<Perforacion> perforacionCollection;
    @OneToMany(mappedBy = "escenarioId")
    private Collection<Pozo> pozoCollection;
    @OneToMany(mappedBy = "escenarioId")
    private Collection<Taladro> taladroCollection;
    @OneToMany(mappedBy = "escenarioId")
    private Collection<Rampeo> rampeoCollection;
    @OneToMany(mappedBy = "escenarioId")
    private Collection<FilaHasTaladro> filaHasTaladroCollection;
    @OneToMany(mappedBy = "escenarioId")
    private Collection<PozoSecuencia> pozoSecuenciaCollection;
    @OneToMany(mappedBy = "escenarioId")
    private Collection<MacollaSecuencia> macollaSecuenciaCollection;
    @OneToMany(mappedBy = "escenarioId")
    private Collection<TaladroHasFase> taladroHasFaseCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "escenarioId")
    private Collection<TaladroAsignado> taladroAsignadoCollection;
    
    @OneToMany(mappedBy = "escenarioId")
    private Collection<ProduccionMesInicial> produccionMesInicialCollection;

    public Escenario() {
    }

    public Escenario(Integer id) {
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Date fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
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
    public Collection<Taladro> getTaladroCollection() {
        return taladroCollection;
    }

    public void setTaladroCollection(Collection<Taladro> taladroCollection) {
        this.taladroCollection = taladroCollection;
    }

    @XmlTransient
    public Collection<Rampeo> getRampeoCollection() {
        return rampeoCollection;
    }

    public void setRampeoCollection(Collection<Rampeo> rampeoCollection) {
        this.rampeoCollection = rampeoCollection;
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

    @XmlTransient
    public Collection<MacollaSecuencia> getMacollaSecuenciaCollection() {
        return macollaSecuenciaCollection;
    }

    public void setMacollaSecuenciaCollection(Collection<MacollaSecuencia> macollaSecuenciaCollection) {
        this.macollaSecuenciaCollection = macollaSecuenciaCollection;
    }

    @XmlTransient
    public Collection<TaladroHasFase> getTaladroHasFaseCollection() {
        return taladroHasFaseCollection;
    }

    public void setTaladroHasFaseCollection(Collection<TaladroHasFase> taladroHasFaseCollection) {
        this.taladroHasFaseCollection = taladroHasFaseCollection;
    }
    
    @XmlTransient
    public Collection<TaladroAsignado> getTaladroAsignadoCollection() {
        return taladroAsignadoCollection;
    }

    public void setTaladroAsignadoCollection(Collection<TaladroAsignado> taladroAsignadoCollection) {
        this.taladroAsignadoCollection = taladroAsignadoCollection;
    }

    @XmlTransient
    public Collection<ProduccionMesInicial> getProduccionMesInicialCollection() {
        return produccionMesInicialCollection;
    }

    public void setProduccionMesInicialCollection(Collection<ProduccionMesInicial> produccionMesInicialCollection) {
        this.produccionMesInicialCollection = produccionMesInicialCollection;
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
        if (!(object instanceof Escenario)) {
            return false;
        }
        Escenario other = (Escenario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    
    
}

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "fila_has_taladro")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FilaHasTaladro.findAll", query = "SELECT f FROM FilaHasTaladro f"),
    @NamedQuery(name = "FilaHasTaladro.findById", query = "SELECT f FROM FilaHasTaladro f WHERE f.id = :id")})
public class FilaHasTaladro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "fecha_asignacion")
    @Temporal(TemporalType.DATE)
    private Date fechaAsignacion;    
    @Column(name = "secuencia")
    private int secuencia;
    @JoinColumn(name = "escenario_id", referencedColumnName = "id")
    @ManyToOne
    private Escenario escenarioId;
    @JoinColumn(name = "fila_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Fila filaId;
    @JoinColumn(name = "taladro_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Taladro taladroId;

    public FilaHasTaladro() {
    }

    public FilaHasTaladro(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public int getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(int secuencia) {
        this.secuencia = secuencia;
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

    public Taladro getTaladroId() {
        return taladroId;
    }

    public void setTaladroId(Taladro taladroId) {
        this.taladroId = taladroId;
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
        if (!(object instanceof FilaHasTaladro)) {
            return false;
        }
        FilaHasTaladro other = (FilaHasTaladro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.FilaHasTaladro[ id=" + id + " ]";
    }
    
}

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
@Table(name = "perforacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Perforacion.findAll", query = "SELECT p FROM Perforacion p"),
    @NamedQuery(name = "Perforacion.findById", query = "SELECT p FROM Perforacion p WHERE p.id = :id"),
    @NamedQuery(name = "Perforacion.findByFase", query = "SELECT p FROM Perforacion p WHERE p.fase = :fase"),
    @NamedQuery(name = "Perforacion.findByFechaIn", query = "SELECT p FROM Perforacion p WHERE p.fechaIn = :fechaIn"),
    @NamedQuery(name = "Perforacion.findByFechaOut", query = "SELECT p FROM Perforacion p WHERE p.fechaOut = :fechaOut"),
    @NamedQuery(name = "Perforacion.findByBs", query = "SELECT p FROM Perforacion p WHERE p.bs = :bs"),
    @NamedQuery(name = "Perforacion.findByUsd", query = "SELECT p FROM Perforacion p WHERE p.usd = :usd"),
    @NamedQuery(name = "Perforacion.findByEquiv", query = "SELECT p FROM Perforacion p WHERE p.equiv = :equiv")})
public class Perforacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "fase")
    private String fase;
    @Column(name = "fecha_in")
    @Temporal(TemporalType.DATE)
    private Date fechaIn;
    @Column(name = "fecha_out")
    @Temporal(TemporalType.DATE)
    private Date fechaOut;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "bs")
    private Double bs;
    @Column(name = "usd")
    private Double usd;
    @Column(name = "equiv")
    private Double equiv;
    @Column(name = "dias")
    private Double dias;
    @Column(name = "dias_activos")
    private Double diasActivos;
    @Column(name = "dias_inactivos")
    private Double diasInactivos;
    @Column(name = "status")
    private Double status;
    @JoinColumn(name = "escenario_id", referencedColumnName = "id")
    @ManyToOne
    private Escenario escenarioId;
    @JoinColumn(name = "fila_id", referencedColumnName = "id")
    @ManyToOne
    private Fila filaId;
    @JoinColumn(name = "macolla_id", referencedColumnName = "id")
    @ManyToOne
    private Macolla macollaId;
    @JoinColumn(name = "pozo_id", referencedColumnName = "id")
    @ManyToOne
    private Pozo pozoId;
    @JoinColumn(name = "taladro_id", referencedColumnName = "id")
    @ManyToOne
    private Taladro taladroId;

    public Perforacion() {
    }

    public Perforacion(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
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

    public Double getBs() {
        return bs;
    }

    public void setBs(Double bs) {
        this.bs = bs;
    }

    public Double getUsd() {
        return usd;
    }

    public void setUsd(Double usd) {
        this.usd = usd;
    }

    public Double getEquiv() {
        return equiv;
    }

    public void setEquiv(Double equiv) {
        this.equiv = equiv;
    }

    public Double getDias() {
        return dias;
    }

    public void setDias(Double dias) {
        this.dias = dias;
    }

    public Double getDiasActivos() {
        return diasActivos;
    }

    public void setDiasActivos(Double diasActivos) {
        this.diasActivos = diasActivos;
    }

    public Double getDiasInactivos() {
        return diasInactivos;
    }

    public void setDiasInactivos(Double diasInactivos) {
        this.diasInactivos = diasInactivos;
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

    public Macolla getMacollaId() {
        return macollaId;
    }

    public void setMacollaId(Macolla macollaId) {
        this.macollaId = macollaId;
    }

    public Pozo getPozoId() {
        return pozoId;
    }

    public void setPozoId(Pozo pozoId) {
        this.pozoId = pozoId;
    }

    public Taladro getTaladroId() {
        return taladroId;
    }

    public void setTaladroId(Taladro taladroId) {
        this.taladroId = taladroId;
    }
    
    public Double getStatus() {
        return status;
    }

    public void setStatus(Double status) {
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
        if (!(object instanceof Perforacion)) {
            return false;
        }
        Perforacion other = (Perforacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.Perforacion[ id=" + id + " ]";
    }
    
}

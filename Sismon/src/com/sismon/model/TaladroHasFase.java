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
@Table(name = "taladro_has_fase")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TaladroHasFase.findAll", query = "SELECT t FROM TaladroHasFase t"),
    @NamedQuery(name = "TaladroHasFase.findById", query = "SELECT t FROM TaladroHasFase t WHERE t.id = :id"),
    @NamedQuery(name = "TaladroHasFase.findByDias", query = "SELECT t FROM TaladroHasFase t WHERE t.dias = :dias"),
    @NamedQuery(name = "TaladroHasFase.findByCostoBs", query = "SELECT t FROM TaladroHasFase t WHERE t.costoBs = :costoBs"),
    @NamedQuery(name = "TaladroHasFase.findByCostoUsd", query = "SELECT t FROM TaladroHasFase t WHERE t.costoUsd = :costoUsd"),
    @NamedQuery(name = "TaladroHasFase.findByCostoEquiv", query = "SELECT t FROM TaladroHasFase t WHERE t.costoEquiv = :costoEquiv"),
    @NamedQuery(name = "TaladroHasFase.findByFase", query = "SELECT t FROM TaladroHasFase t WHERE t.fase = :fase")})
public class TaladroHasFase implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "dias")
    private Double dias;
    @Column(name = "costo_bs")
    private Double costoBs;
    @Column(name = "costo_usd")
    private Double costoUsd;
    @Column(name = "costo_equiv")
    private Double costoEquiv;
    @Column(name = "fase")
    private String fase;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @JoinColumn(name = "escenario_id", referencedColumnName = "id")
    @ManyToOne
    private Escenario escenarioId;
    @JoinColumn(name = "taladro_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Taladro taladroId;

    public TaladroHasFase() {
    }

    public TaladroHasFase(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getDias() {
        return dias;
    }

    public void setDias(Double dias) {
        this.dias = dias;
    }

    public Double getCostoBs() {
        return costoBs;
    }

    public void setCostoBs(Double costoBs) {
        this.costoBs = costoBs;
    }

    public Double getCostoUsd() {
        return costoUsd;
    }

    public void setCostoUsd(Double costoUsd) {
        this.costoUsd = costoUsd;
    }

    public Double getCostoEquiv() {
        return costoEquiv;
    }

    public void setCostoEquiv(Double costoEquiv) {
        this.costoEquiv = costoEquiv;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Escenario getEscenarioId() {
        return escenarioId;
    }

    public void setEscenarioId(Escenario escenarioId) {
        this.escenarioId = escenarioId;
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
        if (!(object instanceof TaladroHasFase)) {
            return false;
        }
        TaladroHasFase other = (TaladroHasFase) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.TaladroHasFase[ id=" + id + " ]";
    }
    
}

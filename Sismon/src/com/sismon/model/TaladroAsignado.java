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
@Table(name = "taladro_asignado")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TaladroAsignado.findAll", query = "SELECT t FROM TaladroAsignado t"),
    @NamedQuery(name = "TaladroAsignado.findById", query = "SELECT t FROM TaladroAsignado t WHERE t.id = :id"),
    @NamedQuery(name = "TaladroAsignado.findByFaseIn", query = "SELECT t FROM TaladroAsignado t WHERE t.faseIn = :faseIn"),
    @NamedQuery(name = "TaladroAsignado.findByFaseOut", query = "SELECT t FROM TaladroAsignado t WHERE t.faseOut = :faseOut")})
public class TaladroAsignado implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "orden")
    private int orden;
    @Column(name = "fase_in")
    private String faseIn;
    @Column(name = "fase_out")
    private String faseOut;
    @JoinColumn(name = "escenario_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Escenario escenarioId;
    @JoinColumn(name = "fila_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Fila filaId;
    @JoinColumn(name = "pozo_in_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Pozo pozoInId;
    @JoinColumn(name = "pozo_out_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Pozo pozoOutId;
    @JoinColumn(name = "pozo_secuencia_in_id", referencedColumnName = "id")
    @ManyToOne
    private PozoSecuencia pozoSecuenciaInId;
    @JoinColumn(name = "pozo_secuencia_out_id", referencedColumnName = "id")
    @ManyToOne
    private PozoSecuencia pozoSecuenciaOutId;
    @JoinColumn(name = "taladro_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Taladro taladroId;

    public TaladroAsignado() {
    }

    public TaladroAsignado(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getFaseIn() {
        return faseIn;
    }

    public void setFaseIn(String faseIn) {
        this.faseIn = faseIn;
    }

    public String getFaseOut() {
        return faseOut;
    }

    public void setFaseOut(String faseOut) {
        this.faseOut = faseOut;
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

    public Pozo getPozoInId() {
        return pozoInId;
    }

    public void setPozoInId(Pozo pozoInId) {
        this.pozoInId = pozoInId;
    }

    public Pozo getPozoOutId() {
        return pozoOutId;
    }

    public void setPozoOutId(Pozo pozoOutId) {
        this.pozoOutId = pozoOutId;
    }

    public PozoSecuencia getPozoSecuenciaInId() {
        return pozoSecuenciaInId;
    }

    public void setPozoSecuenciaInId(PozoSecuencia pozoSecuenciaInId) {
        this.pozoSecuenciaInId = pozoSecuenciaInId;
    }

    public PozoSecuencia getPozoSecuenciaOutId() {
        return pozoSecuenciaOutId;
    }

    public void setPozoSecuenciaOutId(PozoSecuencia pozoSecuenciaOutId) {
        this.pozoSecuenciaOutId = pozoSecuenciaOutId;
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
        if (!(object instanceof TaladroAsignado)) {
            return false;
        }
        TaladroAsignado other = (TaladroAsignado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.TaladroAsignado[ id=" + id + " ]";
    }
    
}

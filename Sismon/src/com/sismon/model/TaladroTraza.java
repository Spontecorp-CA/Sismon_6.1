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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jgcastillo
 */
@Entity
@Table(name = "taladro_traza")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TaladroTraza.findAll", query = "SELECT t FROM TaladroTraza t"),
    @NamedQuery(name = "TaladroTraza.findById", query = "SELECT t FROM TaladroTraza t WHERE t.id = :id"),
    @NamedQuery(name = "TaladroTraza.findByTaladroId", query = "SELECT t FROM TaladroTraza t WHERE t.taladroId = :taladroId"),
    @NamedQuery(name = "TaladroTraza.findByOrden", query = "SELECT t FROM TaladroTraza t WHERE t.orden = :orden"),
    @NamedQuery(name = "TaladroTraza.findByTaladroAsignadoOrigenId", query = "SELECT t FROM TaladroTraza t WHERE t.taladroAsignadoOrigenId = :taladroAsignadoOrigenId"),
    @NamedQuery(name = "TaladroTraza.findByPozoOutorigenid", query = "SELECT t FROM TaladroTraza t WHERE t.pozoOutOrigenId = :pozoOutorigenid"),
    @NamedQuery(name = "TaladroTraza.findByFaseOutOrigen", query = "SELECT t FROM TaladroTraza t WHERE t.faseOutOrigen = :faseOutOrigen"),
    @NamedQuery(name = "TaladroTraza.findByPozoSecuenciaOrigenId", query = "SELECT t FROM TaladroTraza t WHERE t.pozoSecuenciaOrigenId = :pozoSecuenciaOrigenId"),
    @NamedQuery(name = "TaladroTraza.findByTaladroAsignadoDestinoId", query = "SELECT t FROM TaladroTraza t WHERE t.taladroAsignadoDestinoId = :taladroAsignadoDestinoId"),
    @NamedQuery(name = "TaladroTraza.findByTaladroStatusInicialId", query = "SELECT t FROM TaladroTraza t WHERE t.taladroStatusInicialId = :taladroStatusInicialId"),
    @NamedQuery(name = "TaladroTraza.findByTaladroStatusFinalId", query = "SELECT t FROM TaladroTraza t WHERE t.taladroStatusFinalId = :taladroStatusFinalId"),
    @NamedQuery(name = "TaladroTraza.findByEscenarioId", query = "SELECT t FROM TaladroTraza t WHERE t.escenarioId = :escenarioId")})
public class TaladroTraza implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "taladro_id")
    private Long taladroId;
    @Column(name = "orden")
    private Integer orden;
    @Column(name = "taladro_asignado_origen_id")
    private Integer taladroAsignadoOrigenId;
    @Column(name = "pozo_Out_origen_id")
    private Long pozoOutOrigenId;
    @Column(name = "fase_Out_Origen")
    private String faseOutOrigen;
    @Column(name = "pozo_secuencia_origen_id")
    private Long pozoSecuenciaOrigenId;
    @Column(name = "taladro_asignado_destino_id")
    private Integer taladroAsignadoDestinoId;
    @Column(name = "taladro_status_inicial_id")
    private Integer taladroStatusInicialId;
    @Column(name = "taladro_status_final_id")
    private Integer taladroStatusFinalId;
    @Column(name = "escenario_id")
    private Integer escenarioId;

    public TaladroTraza() {
    }

    public TaladroTraza(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getTaladroId() {
        return taladroId;
    }

    public void setTaladroId(Long taladroId) {
        this.taladroId = taladroId;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Integer getTaladroAsignadoOrigenId() {
        return taladroAsignadoOrigenId;
    }

    public void setTaladroAsignadoOrigenId(Integer taladroAsignadoOrigenId) {
        this.taladroAsignadoOrigenId = taladroAsignadoOrigenId;
    }

    public Long getPozoOutOrigenId() {
        return pozoOutOrigenId;
    }

    public void setPozoOutOrigenId(Long pozoOutorigenid) {
        this.pozoOutOrigenId = pozoOutorigenid;
    }

    public String getFaseOutOrigen() {
        return faseOutOrigen;
    }

    public void setFaseOutOrigen(String faseOutOrigen) {
        this.faseOutOrigen = faseOutOrigen;
    }

    public Long getPozoSecuenciaOrigenId() {
        return pozoSecuenciaOrigenId;
    }

    public void setPozoSecuenciaOrigenId(Long pozoSecuenciaOrigenId) {
        this.pozoSecuenciaOrigenId = pozoSecuenciaOrigenId;
    }

    public Integer getTaladroAsignadoDestinoId() {
        return taladroAsignadoDestinoId;
    }

    public void setTaladroAsignadoDestinoId(Integer taladroAsignadoDestinoId) {
        this.taladroAsignadoDestinoId = taladroAsignadoDestinoId;
    }

    public Integer getTaladroStatusInicialId() {
        return taladroStatusInicialId;
    }

    public void setTaladroStatusInicialId(Integer taladroStatusInicialId) {
        this.taladroStatusInicialId = taladroStatusInicialId;
    }

    public Integer getTaladroStatusFinalId() {
        return taladroStatusFinalId;
    }

    public void setTaladroStatusFinalId(Integer taladroStatusFinalId) {
        this.taladroStatusFinalId = taladroStatusFinalId;
    }

    public Integer getEscenarioId() {
        return escenarioId;
    }

    public void setEscenarioId(Integer escenarioId) {
        this.escenarioId = escenarioId;
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
        if (!(object instanceof TaladroTraza)) {
            return false;
        }
        TaladroTraza other = (TaladroTraza) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.TaladroTraza[ id=" + id + " ]";
    }
    
}

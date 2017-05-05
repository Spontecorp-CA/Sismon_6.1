/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sismon.model;

import java.io.Serializable;
import java.math.BigInteger;
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
@Table(name = "taladro_status")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TaladroStatus.findAll", query = "SELECT t FROM TaladroStatus t"),
    @NamedQuery(name = "TaladroStatus.findById", query = "SELECT t FROM TaladroStatus t WHERE t.id = :id"),
    @NamedQuery(name = "TaladroStatus.findByNombre", query = "SELECT t FROM TaladroStatus t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "TaladroStatus.findByFechaIn", query = "SELECT t FROM TaladroStatus t WHERE t.fechaIn = :fechaIn"),
    @NamedQuery(name = "TaladroStatus.findByFechaOut", query = "SELECT t FROM TaladroStatus t WHERE t.fechaOut = :fechaOut"),
    @NamedQuery(name = "TaladroStatus.findByStatus", query = "SELECT t FROM TaladroStatus t WHERE t.status = :status"),
    @NamedQuery(name = "TaladroStatus.findByFilaId", query = "SELECT t FROM TaladroStatus t WHERE t.filaId = :filaId"),
    @NamedQuery(name = "TaladroStatus.findByPozoId", query = "SELECT t FROM TaladroStatus t WHERE t.pozoId = :pozoId"),
    @NamedQuery(name = "TaladroStatus.findByFase", query = "SELECT t FROM TaladroStatus t WHERE t.fase = :fase")})
public class TaladroStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "fecha_in")
    @Temporal(TemporalType.DATE)
    private Date fechaIn;
    @Column(name = "fecha_out")
    @Temporal(TemporalType.DATE)
    private Date fechaOut;
    @Column(name = "status")
    private Integer status;
    @Column(name = "fila_id")
    private Integer filaId;
    @Column(name = "pozo_id")
    private BigInteger pozoId;
    @Column(name = "fase")
    private String fase;
    @JoinColumn(name = "taladro_id", referencedColumnName = "id")
    @ManyToOne
    private Taladro taladroId;

    public TaladroStatus() {
    }

    public TaladroStatus(Integer id) {
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getFilaId() {
        return filaId;
    }

    public void setFilaId(Integer filaId) {
        this.filaId = filaId;
    }

    public BigInteger getPozoId() {
        return pozoId;
    }

    public void setPozoId(BigInteger pozoId) {
        this.pozoId = pozoId;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
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
        if (!(object instanceof TaladroStatus)) {
            return false;
        }
        TaladroStatus other = (TaladroStatus) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.TaladroStatus[ id=" + id + " ]";
    }
    
}

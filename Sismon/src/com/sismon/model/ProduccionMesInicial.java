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
@Table(name = "produccion_mes_inicial")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProduccionMesInicial.findAll", query = "SELECT p FROM ProduccionMesInicial p"),
    @NamedQuery(name = "ProduccionMesInicial.findById", query = "SELECT p FROM ProduccionMesInicial p WHERE p.id = :id"),
    @NamedQuery(name = "ProduccionMesInicial.findByFechaAceptacion", query = "SELECT p FROM ProduccionMesInicial p WHERE p.fechaAceptacion = :fechaAceptacion"),
    @NamedQuery(name = "ProduccionMesInicial.findByProduccion", query = "SELECT p FROM ProduccionMesInicial p WHERE p.produccion = :produccion")})
public class ProduccionMesInicial implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha_aceptacion")
    @Temporal(TemporalType.DATE)
    private Date fechaAceptacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "produccion")
    private Double produccion;
    @JoinColumn(name = "pozo_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Pozo pozoId;
    @JoinColumn(name = "escenario_id", referencedColumnName = "id")
    @ManyToOne
    private Escenario escenarioId;

    public ProduccionMesInicial() {
    }

    public ProduccionMesInicial(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaAceptacion() {
        return fechaAceptacion;
    }

    public void setFechaAceptacion(Date fechaAceptacion) {
        this.fechaAceptacion = fechaAceptacion;
    }

    public Double getProduccion() {
        return produccion;
    }

    public void setProduccion(Double produccion) {
        this.produccion = produccion;
    }

    public Pozo getPozoId() {
        return pozoId;
    }

    public void setPozoId(Pozo pozoId) {
        this.pozoId = pozoId;
    }

    public Escenario getEscenarioId() {
        return escenarioId;
    }

    public void setEscenarioId(Escenario escenarioId) {
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
        if (!(object instanceof ProduccionMesInicial)) {
            return false;
        }
        ProduccionMesInicial other = (ProduccionMesInicial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.ProduccionMesInicial[ id=" + id + " ]";
    }
    
}

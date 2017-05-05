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
@Table(name = "taladro_mant")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TaladroMant.findAll", query = "SELECT t FROM TaladroMant t"),
    @NamedQuery(name = "TaladroMant.findById", query = "SELECT t FROM TaladroMant t WHERE t.id = :id"),
    @NamedQuery(name = "TaladroMant.findByFecha", query = "SELECT t FROM TaladroMant t WHERE t.fecha = :fecha"),
    @NamedQuery(name = "TaladroMant.findByDias", query = "SELECT t FROM TaladroMant t WHERE t.dias = :dias")})
public class TaladroMant implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "dias")
    private Integer dias;
    @JoinColumn(name = "taladro_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Taladro taladroId;

    public TaladroMant() {
    }

    public TaladroMant(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getDias() {
        return dias;
    }

    public void setDias(Integer dias) {
        this.dias = dias;
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
        if (!(object instanceof TaladroMant)) {
            return false;
        }
        TaladroMant other = (TaladroMant) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.TaladroMant[ id=" + id + " ]";
    }
    
}

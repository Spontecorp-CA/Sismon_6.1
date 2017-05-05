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
@Table(name = "taladro_sustituto")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TaladroSustituto.findAll", query = "SELECT t FROM TaladroSustituto t"),
    @NamedQuery(name = "TaladroSustituto.findById", query = "SELECT t FROM TaladroSustituto t WHERE t.id = :id"),
    @NamedQuery(name = "TaladroSustituto.findByFecha", query = "SELECT t FROM TaladroSustituto t WHERE t.fecha = :fecha")})
public class TaladroSustituto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @JoinColumn(name = "taladro_original", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Taladro taladroOriginal;
    @JoinColumn(name = "taladro_sutituto", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Taladro taladroSutituto;

    public TaladroSustituto() {
    }

    public TaladroSustituto(Integer id) {
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

    public Taladro getTaladroOriginal() {
        return taladroOriginal;
    }

    public void setTaladroOriginal(Taladro taladroOriginal) {
        this.taladroOriginal = taladroOriginal;
    }

    public Taladro getTaladroSutituto() {
        return taladroSutituto;
    }

    public void setTaladroSutituto(Taladro taladroSutituto) {
        this.taladroSutituto = taladroSutituto;
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
        if (!(object instanceof TaladroSustituto)) {
            return false;
        }
        TaladroSustituto other = (TaladroSustituto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.TaladroSustituto[ id=" + id + " ]";
    }
    
}

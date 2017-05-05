/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sismon.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author jgcastillo
 */
@Entity
@Table(name = "macolla")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Macolla.findAll", query = "SELECT m FROM Macolla m"),
    @NamedQuery(name = "Macolla.findById", query = "SELECT m FROM Macolla m WHERE m.id = :id"),
    @NamedQuery(name = "Macolla.findByNombre", query = "SELECT m FROM Macolla m WHERE m.nombre = :nombre"),
    @NamedQuery(name = "Macolla.findByNumero", query = "SELECT m FROM Macolla m WHERE m.numero = :numero"),
    @NamedQuery(name = "Macolla.findByCostoLocalizacionBs", query = "SELECT m FROM Macolla m WHERE m.costoLocalizacionBs = :costoLocalizacionBs"),
    @NamedQuery(name = "Macolla.findByCostoLocalizacionUsd", query = "SELECT m FROM Macolla m WHERE m.costoLocalizacionUsd = :costoLocalizacionUsd")})
public class Macolla implements Serializable, Comparable<Macolla> {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "macollaId")
    private Collection<MacollaExplotada> macollaExplotadaCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "numero")
    private Integer numero;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "costo_localizacion_bs")
    private Double costoLocalizacionBs;
    @Column(name = "costo_localizacion_usd")
    private Double costoLocalizacionUsd;
    @OneToMany(mappedBy = "macollaId")
    private Collection<Perforacion> perforacionCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "macollaId")
    private Collection<Pozo> pozoCollection;
    @JoinColumn(name = "campo_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Campo campoId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "macollaId")
    private Collection<MacollaSecuencia> macollaSecuenciaCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "macollaId")
    private Collection<Fila> filaCollection;

    public Macolla() {
    }

    public Macolla(Integer id) {
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

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Double getCostoLocalizacionBs() {
        return costoLocalizacionBs;
    }

    public void setCostoLocalizacionBs(Double costoLocalizacionBs) {
        this.costoLocalizacionBs = costoLocalizacionBs;
    }

    public Double getCostoLocalizacionUsd() {
        return costoLocalizacionUsd;
    }

    public void setCostoLocalizacionUsd(Double costoLocalizacionUsd) {
        this.costoLocalizacionUsd = costoLocalizacionUsd;
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

    public Campo getCampoId() {
        return campoId;
    }

    public void setCampoId(Campo campoId) {
        this.campoId = campoId;
    }

    @XmlTransient
    public Collection<MacollaSecuencia> getMacollaSecuenciaCollection() {
        return macollaSecuenciaCollection;
    }

    public void setMacollaSecuenciaCollection(Collection<MacollaSecuencia> macollaSecuenciaCollection) {
        this.macollaSecuenciaCollection = macollaSecuenciaCollection;
    }

    @XmlTransient
    public Collection<Fila> getFilaCollection() {
        return filaCollection;
    }

    public void setFilaCollection(Collection<Fila> filaCollection) {
        this.filaCollection = filaCollection;
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
        if (!(object instanceof Macolla)) {
            return false;
        }
        Macolla other = (Macolla) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        if (!this.nombre.equalsIgnoreCase(other.nombre) || this.numero != other.numero) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.nombre).append(" - (");
        sb.append("Macolla-").append(this.numero).append(")");
        return sb.toString();
    }

    @Override
    public int compareTo(Macolla other) {
        return Integer.compare(this.numero, other.numero);
    }

    @XmlTransient
    public Collection<MacollaExplotada> getMacollaExplotadaCollection() {
        return macollaExplotadaCollection;
    }

    public void setMacollaExplotadaCollection(Collection<MacollaExplotada> macollaExplotadaCollection) {
        this.macollaExplotadaCollection = macollaExplotadaCollection;
    }
    
}

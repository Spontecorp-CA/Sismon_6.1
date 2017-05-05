/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sismon.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
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
@Table(name = "tipo_pozo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TipoPozo.findAll", query = "SELECT t FROM TipoPozo t"),
    @NamedQuery(name = "TipoPozo.findById", query = "SELECT t FROM TipoPozo t WHERE t.id = :id"),
    @NamedQuery(name = "TipoPozo.findByTipo", query = "SELECT t FROM TipoPozo t WHERE t.tipo = :tipo"),
    @NamedQuery(name = "TipoPozo.findByCodigo", query = "SELECT t FROM TipoPozo t WHERE t.codigo = :codigo")})
public class TipoPozo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "tipo")
    private String tipo;
    @Column(name = "codigo")
    private String codigo;
    @Lob
    @Column(name = "imagen")
    private String imagen;

    public TipoPozo() {
    }

    public TipoPozo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
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
        if (!(object instanceof TipoPozo)) {
            return false;
        }
        TipoPozo other = (TipoPozo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.TipoPozo[ id=" + id + " ]";
    }
    
}

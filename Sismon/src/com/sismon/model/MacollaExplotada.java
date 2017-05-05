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
@Table(name = "macolla_explotada")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MacollaExplotada.findAll", query = "SELECT m FROM MacollaExplotada m"),
    @NamedQuery(name = "MacollaExplotada.findById", query = "SELECT m FROM MacollaExplotada m WHERE m.id = :id")})
public class MacollaExplotada implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "macolla_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Macolla macollaId;

    public MacollaExplotada() {
    }

    public MacollaExplotada(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Macolla getMacollaId() {
        return macollaId;
    }

    public void setMacollaId(Macolla macollaId) {
        this.macollaId = macollaId;
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
        if (!(object instanceof MacollaExplotada)) {
            return false;
        }
        MacollaExplotada other = (MacollaExplotada) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        if(!this.getMacollaId().equals(other.getMacollaId())){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.MacollaExplotada[ id=" + id + " ]";
    }
    
}

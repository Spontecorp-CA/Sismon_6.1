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
@Table(name = "pozo_explotado")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PozoExplotado.findAll", query = "SELECT p FROM PozoExplotado p"),
    @NamedQuery(name = "PozoExplotado.findById", query = "SELECT p FROM PozoExplotado p WHERE p.id = :id")})
public class PozoExplotado implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "pozo_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Pozo pozoId;

    public PozoExplotado() {
    }

    public PozoExplotado(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pozo getPozoId() {
        return pozoId;
    }

    public void setPozoId(Pozo pozoId) {
        this.pozoId = pozoId;
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
        if (!(object instanceof PozoExplotado)) {
            return false;
        }
        PozoExplotado other = (PozoExplotado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        if (!this.getPozoId().equals(other.getPozoId())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.PozoExplotado[ id=" + id + " ]";
    }
    
}

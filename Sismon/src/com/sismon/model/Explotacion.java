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
@Table(name = "explotacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Explotacion.findAll", query = "SELECT e FROM Explotacion e"),
    @NamedQuery(name = "Explotacion.findById", query = "SELECT e FROM Explotacion e WHERE e.id = :id"),
    @NamedQuery(name = "Explotacion.findByFecha", query = "SELECT e FROM Explotacion e WHERE e.fecha = :fecha"),
    @NamedQuery(name = "Explotacion.findByProdDiaria", query = "SELECT e FROM Explotacion e WHERE e.prodDiaria = :prodDiaria"),
    @NamedQuery(name = "Explotacion.findByProdAcum", query = "SELECT e FROM Explotacion e WHERE e.prodAcum = :prodAcum"),
    @NamedQuery(name = "Explotacion.findByProdGas", query = "SELECT e FROM Explotacion e WHERE e.prodGas = :prodGas"),
    @NamedQuery(name = "Explotacion.findByProdGasAcum", query = "SELECT e FROM Explotacion e WHERE e.prodGasAcum = :prodGasAcum"),
    @NamedQuery(name = "Explotacion.findByProdAyS", query = "SELECT e FROM Explotacion e WHERE e.prodAyS = :prodAyS"),
    @NamedQuery(name = "Explotacion.findByProdAySAcum", query = "SELECT e FROM Explotacion e WHERE e.prodAySAcum = :prodAySAcum"),
    @NamedQuery(name = "Explotacion.findByProdDlnt", query = "SELECT e FROM Explotacion e WHERE e.prodDlnt = :prodDlnt"),
    @NamedQuery(name = "Explotacion.findByProdDlntAcum", query = "SELECT e FROM Explotacion e WHERE e.prodDlntAcum = :prodDlntAcum")})
public class Explotacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "prodDiaria")
    private Double prodDiaria;
    @Column(name = "prodAcum")
    private Double prodAcum;
    @Column(name = "prodGas")
    private Double prodGas;
    @Column(name = "prodGasAcum")
    private Double prodGasAcum;
    @Column(name = "prodAyS")
    private Double prodAyS;
    @Column(name = "prodAySAcum")
    private Double prodAySAcum;
    @Column(name = "prodDlnt")
    private Double prodDlnt;
    @Column(name = "prodDlntAcum")
    private Double prodDlntAcum;
    @JoinColumn(name = "pozo_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Pozo pozoId;

    public Explotacion() {
    }

    public Explotacion(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getProdDiaria() {
        return prodDiaria;
    }

    public void setProdDiaria(Double prodDiaria) {
        this.prodDiaria = prodDiaria;
    }

    public Double getProdAcum() {
        return prodAcum;
    }

    public void setProdAcum(Double prodAcum) {
        this.prodAcum = prodAcum;
    }

    public Double getProdGas() {
        return prodGas;
    }

    public void setProdGas(Double prodGas) {
        this.prodGas = prodGas;
    }

    public Double getProdGasAcum() {
        return prodGasAcum;
    }

    public void setProdGasAcum(Double prodGasAcum) {
        this.prodGasAcum = prodGasAcum;
    }

    public Double getProdAyS() {
        return prodAyS;
    }

    public void setProdAyS(Double prodAyS) {
        this.prodAyS = prodAyS;
    }

    public Double getProdAySAcum() {
        return prodAySAcum;
    }

    public void setProdAySAcum(Double prodAySAcum) {
        this.prodAySAcum = prodAySAcum;
    }

    public Double getProdDlnt() {
        return prodDlnt;
    }

    public void setProdDlnt(Double prodDlnt) {
        this.prodDlnt = prodDlnt;
    }

    public Double getProdDlntAcum() {
        return prodDlntAcum;
    }

    public void setProdDlntAcum(Double prodDlntAcum) {
        this.prodDlntAcum = prodDlntAcum;
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
        if (!(object instanceof Explotacion)) {
            return false;
        }
        Explotacion other = (Explotacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sismon.model.Explotacion[ id=" + id + " ]";
    }
    
}

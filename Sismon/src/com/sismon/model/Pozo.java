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
@Table(name = "pozo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pozo.findAll", query = "SELECT p FROM Pozo p"),
    @NamedQuery(name = "Pozo.findById", query = "SELECT p FROM Pozo p WHERE p.id = :id"),
    @NamedQuery(name = "Pozo.findByUbicacion", query = "SELECT p FROM Pozo p WHERE p.ubicacion = :ubicacion"),
    @NamedQuery(name = "Pozo.findByNumero", query = "SELECT p FROM Pozo p WHERE p.numero = :numero"),
    @NamedQuery(name = "Pozo.findByNombre", query = "SELECT p FROM Pozo p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Pozo.findByPlan", query = "SELECT p FROM Pozo p WHERE p.plan = :plan"),
    @NamedQuery(name = "Pozo.findByClasePozo", query = "SELECT p FROM Pozo p WHERE p.clasePozo = :clasePozo"),
    @NamedQuery(name = "Pozo.findByYacimiento", query = "SELECT p FROM Pozo p WHERE p.yacimiento = :yacimiento"),
    @NamedQuery(name = "Pozo.findByBloque", query = "SELECT p FROM Pozo p WHERE p.bloque = :bloque"),
    @NamedQuery(name = "Pozo.findByPi", query = "SELECT p FROM Pozo p WHERE p.pi = :pi"),
    @NamedQuery(name = "Pozo.findByDeclinacion", query = "SELECT p FROM Pozo p WHERE p.declinacion = :declinacion"),
    @NamedQuery(name = "Pozo.findByInicioDecl", query = "SELECT p FROM Pozo p WHERE p.inicioDecl = :inicioDecl"),
    @NamedQuery(name = "Pozo.findByRgp", query = "SELECT p FROM Pozo p WHERE p.rgp = :rgp"),
    @NamedQuery(name = "Pozo.findByIncremAnualRgp", query = "SELECT p FROM Pozo p WHERE p.incremAnualRgp = :incremAnualRgp"),
    @NamedQuery(name = "Pozo.findByInicioDeclRgp", query = "SELECT p FROM Pozo p WHERE p.inicioDeclRgp = :inicioDeclRgp"),
    @NamedQuery(name = "Pozo.findByAys", query = "SELECT p FROM Pozo p WHERE p.ays = :ays"),
    @NamedQuery(name = "Pozo.findByIncremAnualAys", query = "SELECT p FROM Pozo p WHERE p.incremAnualAys = :incremAnualAys"),
    @NamedQuery(name = "Pozo.findByInicioDeclAys", query = "SELECT p FROM Pozo p WHERE p.inicioDeclAys = :inicioDeclAys"),
    @NamedQuery(name = "Pozo.findByExpHiperb", query = "SELECT p FROM Pozo p WHERE p.expHiperb = :expHiperb"),
    @NamedQuery(name = "Pozo.findByTasaAbandono", query = "SELECT p FROM Pozo p WHERE p.tasaAbandono = :tasaAbandono"),
    @NamedQuery(name = "Pozo.findByReservaMax", query = "SELECT p FROM Pozo p WHERE p.reservaMax = :reservaMax"),
    @NamedQuery(name = "Pozo.findByGradoApiXp", query = "SELECT p FROM Pozo p WHERE p.gradoApiXp = :gradoApiXp"),
    @NamedQuery(name = "Pozo.findByGradoApiDiluente", query = "SELECT p FROM Pozo p WHERE p.gradoApiDiluente = :gradoApiDiluente"),
    @NamedQuery(name = "Pozo.findByGradoApiMezcla", query = "SELECT p FROM Pozo p WHERE p.gradoApiMezcla = :gradoApiMezcla")})
public class Pozo implements Serializable, Comparable<Pozo> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "ubicacion")
    private String ubicacion;
    @Column(name = "numero")
    private Integer numero;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "plan")
    private String plan;
    @Column(name = "clase_pozo")
    private String clasePozo;
    @Column(name = "tipo_pozo")
    private String tipoPozo;
    @Column(name = "codigo_pozo")
    private String codigoPozo;
    @Column(name = "yacimiento")
    private String yacimiento;
    @Column(name = "bloque")
    private String bloque;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "pi")
    private Double pi;
    @Column(name = "declinacion")
    private Double declinacion;
    @Column(name = "inicio_decl")
    private Integer inicioDecl;
    @Column(name = "rgp")
    private Double rgp;
    @Column(name = "increm_anual_rgp")
    private Double incremAnualRgp;
    @Column(name = "inicio_decl_rgp")
    private Integer inicioDeclRgp;
    @Column(name = "ays")
    private Double ays;
    @Column(name = "increm_anual_ays")
    private Double incremAnualAys;
    @Column(name = "inicio_decl_ays")
    private Integer inicioDeclAys;
    @Column(name = "exp_hiperb")
    private Double expHiperb;
    @Column(name = "tasa_abandono")
    private Double tasaAbandono;
    @Column(name = "reserva_max")
    private Double reservaMax;
    @Column(name = "grado_api_xp")
    private Double gradoApiXp;
    @Column(name = "grado_api_diluente")
    private Double gradoApiDiluente;
    @Column(name = "grado_api_mezcla")
    private Double gradoApiMezcla;
    @OneToMany(mappedBy = "pozoId")
    private Collection<Perforacion> perforacionCollection;
    @JoinColumn(name = "escenario_id", referencedColumnName = "id")
    @ManyToOne
    private Escenario escenarioId;
    @JoinColumn(name = "fila_id", referencedColumnName = "id")
    @ManyToOne
    private Fila filaId;
    @JoinColumn(name = "macolla_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Macolla macollaId;
    @OneToMany(mappedBy = "pozoId", cascade = CascadeType.ALL)
    private Collection<Rampeo> rampeoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pozoId")
    private Collection<PozoSecuencia> pozoSecuenciaCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pozoId")
    private Collection<Explotacion> explotacionCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pozoId")
    private Collection<PozoExplotado> pozoExplotadoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pozoInId")
    private Collection<TaladroAsignado> taladroAsignadoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pozoId")
    private Collection<ProduccionMesInicial> produccionMesInicialCollection;

    public Pozo() {
    }

    public Pozo(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getClasePozo() {
        return clasePozo;
    }

    public void setClasePozo(String clasePozo) {
        this.clasePozo = clasePozo;
    }

    public String getYacimiento() {
        return yacimiento;
    }

    public void setYacimiento(String yacimiento) {
        this.yacimiento = yacimiento;
    }

    public String getBloque() {
        return bloque;
    }

    public void setBloque(String bloque) {
        this.bloque = bloque;
    }

    public Double getPi() {
        return pi;
    }

    public void setPi(Double pi) {
        this.pi = pi;
    }

    public Double getDeclinacion() {
        return declinacion;
    }

    public void setDeclinacion(Double declinacion) {
        this.declinacion = declinacion;
    }

    public Integer getInicioDecl() {
        return inicioDecl;
    }

    public void setInicioDecl(Integer inicioDecl) {
        this.inicioDecl = inicioDecl;
    }

    public Double getRgp() {
        return rgp;
    }

    public void setRgp(Double rgp) {
        this.rgp = rgp;
    }

    public Double getIncremAnualRgp() {
        return incremAnualRgp;
    }

    public void setIncremAnualRgp(Double incremAnualRgp) {
        this.incremAnualRgp = incremAnualRgp;
    }

    public Integer getInicioDeclRgp() {
        return inicioDeclRgp;
    }

    public void setInicioDeclRgp(Integer inicioDeclRgp) {
        this.inicioDeclRgp = inicioDeclRgp;
    }

    public Double getAys() {
        return ays;
    }

    public void setAys(Double ays) {
        this.ays = ays;
    }

    public Double getIncremAnualAys() {
        return incremAnualAys;
    }

    public void setIncremAnualAys(Double incremAnualAys) {
        this.incremAnualAys = incremAnualAys;
    }

    public Integer getInicioDeclAys() {
        return inicioDeclAys;
    }

    public void setInicioDeclAys(Integer inicioDeclAys) {
        this.inicioDeclAys = inicioDeclAys;
    }

    public Double getExpHiperb() {
        return expHiperb;
    }

    public void setExpHiperb(Double expHiperb) {
        this.expHiperb = expHiperb;
    }

    public Double getTasaAbandono() {
        return tasaAbandono;
    }

    public void setTasaAbandono(Double tasaAbandono) {
        this.tasaAbandono = tasaAbandono;
    }

    public Double getReservaMax() {
        return reservaMax;
    }

    public void setReservaMax(Double reservaMax) {
        this.reservaMax = reservaMax;
    }

    public Double getGradoApiXp() {
        return gradoApiXp;
    }

    public void setGradoApiXp(Double gradoApiXp) {
        this.gradoApiXp = gradoApiXp;
    }

    public Double getGradoApiDiluente() {
        return gradoApiDiluente;
    }

    public void setGradoApiDiluente(Double gradoApiDiluente) {
        this.gradoApiDiluente = gradoApiDiluente;
    }

    public Double getGradoApiMezcla() {
        return gradoApiMezcla;
    }

    public void setGradoApiMezcla(Double gradoApiMezcla) {
        this.gradoApiMezcla = gradoApiMezcla;
    }

    @XmlTransient
    public Collection<Perforacion> getPerforacionCollection() {
        return perforacionCollection;
    }

    public void setPerforacionCollection(Collection<Perforacion> perforacionCollection) {
        this.perforacionCollection = perforacionCollection;
    }

    public Escenario getEscenarioId() {
        return escenarioId;
    }

    public void setEscenarioId(Escenario escenarioId) {
        this.escenarioId = escenarioId;
    }

    public Fila getFilaId() {
        return filaId;
    }

    public void setFilaId(Fila filaId) {
        this.filaId = filaId;
    }

    public Macolla getMacollaId() {
        return macollaId;
    }

    public void setMacollaId(Macolla macollaId) {
        this.macollaId = macollaId;
    }

    @XmlTransient
    public Collection<Rampeo> getRampeoCollection() {
        return rampeoCollection;
    }

    public void setRampeoCollection(Collection<Rampeo> rampeoCollection) {
        this.rampeoCollection = rampeoCollection;
    }

    @XmlTransient
    public Collection<PozoSecuencia> getPozoSecuenciaCollection() {
        return pozoSecuenciaCollection;
    }

    public void setPozoSecuenciaCollection(Collection<PozoSecuencia> pozoSecuenciaCollection) {
        this.pozoSecuenciaCollection = pozoSecuenciaCollection;
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
        if (!(object instanceof Pozo)) {
            return false;
        }
        Pozo other = (Pozo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        if (!this.ubicacion.equalsIgnoreCase(other.ubicacion)) {
            return false;
        }
        if (this.nombre != null && other.nombre != null) {
            if (!this.nombre.equalsIgnoreCase(other.nombre)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return ubicacion;
    }

    @Override
    public int compareTo(Pozo other) {
        return Integer.compare(this.numero, other.numero);
    }

    @XmlTransient
    public Collection<Explotacion> getExplotacionCollection() {
        return explotacionCollection;
    }

    public void setExplotacionCollection(Collection<Explotacion> explotacionCollection) {
        this.explotacionCollection = explotacionCollection;
    }

    @XmlTransient
    public Collection<PozoExplotado> getPozoExplotadoCollection() {
        return pozoExplotadoCollection;
    }

    public void setPozoExplotadoCollection(Collection<PozoExplotado> pozoExplotadoCollection) {
        this.pozoExplotadoCollection = pozoExplotadoCollection;
    }

    public String getTipoPozo() {
        return tipoPozo;
    }

    public void setTipoPozo(String tipoPozo) {
        this.tipoPozo = tipoPozo;
    }

    public String getCodigoPozo() {
        return codigoPozo;
    }

    public void setCodigoPozo(String codigoPozo) {
        this.codigoPozo = codigoPozo;
    }

    @XmlTransient
    public Collection<TaladroAsignado> getTaladroAsignadoCollection() {
        return taladroAsignadoCollection;
    }

    public void setTaladroAsignadoCollection(Collection<TaladroAsignado> taladroAsignadoCollection) {
        this.taladroAsignadoCollection = taladroAsignadoCollection;
    }

    @XmlTransient
    public Collection<ProduccionMesInicial> getProduccionMesInicialCollection() {
        return produccionMesInicialCollection;
    }

    public void setProduccionMesInicialCollection(Collection<ProduccionMesInicial> produccionMesInicialCollection) {
        this.produccionMesInicialCollection = produccionMesInicialCollection;
    }

}

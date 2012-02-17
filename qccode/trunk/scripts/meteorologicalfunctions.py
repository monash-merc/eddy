import constants as c
import numpy

def densitydryair(Ta,ps):
    # Calculate density of dry air from absolute humidity and temperature
    #  Ta - air temperature, C
    #  ps - pressure, kPa
    # Returns
    #  rhod - dry air density, kg/m3
    rhod = ps*1000/((Ta+273.15)*c.Rd)
    return rhod

def densitymoistair(Ta,ps,Ah):
    # Calculate density of  air from absolute humidity and temperature
    #  Ta - air temperature, C
    #  ps - pressure, kPa
    #  Ah - absolute humidity, g/m3
    # Returns
    #  rhom - moist air density, kg/m3
    vp = vapourpressure(Ah,Ta)
    rhom = (ps-vp)*1000/((Ta+273.15)*c.Rd) + vp*1000/((Ta+273.15)*c.Rv)
    return rhom

def es(T):
    # Saturation vapour pressure.
    #  T is the air temperature, C
    #  es is the saturation vapour pressure in kPa
    es = 0.6106 * numpy.exp(17.27 * T / (T + 237.3))
    return es

def Lv(Ta):
    # Calculate Lv as a function of temperature, from Stull 1988
    #  Ta - air temperature, C
    # Returns
    #  Lv - latent heat of vapourisation, J/kg
    Lv = 2500800 - (2366.8 * Ta)
    return Lv

def mixingratio(ps,vp):
    # Calculate mixing ratio from vapour pressure and pressure
    #  ps - presure, kPa
    #  vp - vapour pressure, kPa
    # Returns
    #  mr - mixing ratio, kg/kg
    mr = 0.622*vp/(ps- vp)
    return mr

def molen(T,Ah,p,ustar,Fh):
    # Calculate the Monin-Obukhov length
    ustar = numpy.sqrt(ustar*ustar)
    L = -theta(T, p)*densitydryair(T, p)*c.Cp*(ustar**3)/(c.g*c.k*Fh)
    return L

def molenv(Tv,ustar,Fhv):
    # Calculate the Obukhov length using sonic virtual temperature measurement
    L = -(Tv * ustar ** 3) / (c.k * c.g * Fhv)
    return L

def qfromrh(RH, T, p):
    # Specific humidity (kg/kg) from relative humidity, temperature and pressure
    #  RH is the relative humidity, %
    #  T is the air temperature, C
    #  p is the atmospheric pressure, kPa
    qRH = (c.Mv / c.Md) * (0.01 * RH * es(T) / p)
    return qRH

def specificheatmoistair(q):
    # Calculate Cp of moist air, from Stull 1988
    #  Cp - specific heat of dry air at constant pressure, J/kg-K
    #  q - specific humidity
    # Returns
    #  Cpm - specific heat of moist air at constant pressure, J/kg-K
    Cpm = c.Cpd * (1 + 0.84 * q)
    return Cpm

def specifichumidity(mr):
    # Calculate specific humidity from mixing ratio
    #  mr - mixing ration, kg/kg
    # Returns
    #  q = specific humidity, kg/kg
    q = mr/(1+mr)
    return q

def tafromtv(Tv,q):
    # Calculate air temperature from virtual temperature using formula
    # from Campbell Scientific CSAT manual.
    # NOTE: this differs from the usual definition by using 0.51 not 0.61
    #  Tv - virtual temperature, C
    #  q - specific humidity, kg/kg
    # Returns
    #  Ta - air temperature, C
    Ta = ((Tv+273.15)/(1+0.51*q))-273.15
    return Ta

def theta(T,p):
    # Calculate potential temperature from air temperature and pressure
    #  T - air temperature, C
    #  p - pressure, kPa
    # Returns
    #  theta - potential temperature, K
    return (T+273.15)*(100/p)**0.286
def vapourpressure(Ah,Ta):
    # Calculate vapour pressure from absolute humidity and temperature
    #  Ah - absolute humidity, g/m3
    #  Ta - air temperature, C
    # Returns
    #  vp - vapour pressure, kPa
    vp = 0.0000001*Ah*(Ta+273.15)*c.R/c.Mv
    return vp


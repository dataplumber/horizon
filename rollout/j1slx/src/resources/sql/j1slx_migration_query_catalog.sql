insert into j1slx_catalogentry (id, version, product_version, cycle,author, email_id,
cnes_approval, nasa_approval, gdr_arch_time,
 gdr_date, gdrnc_date, gdrnc_staged, sgdr_date, sgdr_staged, 
 sgdrnc_date, sgdrnc_staged, 
 sshanc_date, sshanc_staged
)

select HIBERNATE_SEQUENCE.NEXTVAL,0, VERSION, cycle, DATA_AUTHOR,EMAIL_ID,
apflag(cnes_approved), apflag(nasa_approved), 0 , 
dateString(gdr_release_date), dateString(gdrnetcdf_release_date), apflag(GDRNETCDF_STAGED),
dateString(sgdr_release_date), apflag(sgdr_staged),
dateString(sgdrnetcdf_release_date), apflag(sgdrnetcdf_staged),
dateString(sshanetcdf_release_date), apflag(sshanetcdf_staged)
from catalog;


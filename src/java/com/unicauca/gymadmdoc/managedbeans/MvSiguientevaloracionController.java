package com.unicauca.gymadmdoc.managedbeans;

import com.unicauca.gymadmdoc.entities.MvSiguientevaloracion;
import com.unicauca.gymadmdoc.managedbeans.util.JsfUtil;
import com.unicauca.gymadmdoc.managedbeans.util.JsfUtil.PersistAction;
import com.unicauca.gymadmdoc.sessionbeans.MvSiguientevaloracionFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("mvSiguientevaloracionController")
@SessionScoped
public class MvSiguientevaloracionController implements Serializable {

    @EJB
    private com.unicauca.gymadmdoc.sessionbeans.MvSiguientevaloracionFacade ejbFacade;
    private List<MvSiguientevaloracion> items = null;
    private MvSiguientevaloracion selected;

    public MvSiguientevaloracionController() {}

    public MvSiguientevaloracion getSelected() {
        return selected;
    }

    public void setSelected(MvSiguientevaloracion selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {}

    protected void initializeEmbeddableKey() {}

    private MvSiguientevaloracionFacade getFacade() {
        return ejbFacade;
    }

    public MvSiguientevaloracion prepareCreate() {
        selected = new MvSiguientevaloracion();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/mvMensajes").getString("MvSiguientevaloracionCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/mvMensajes").getString("MvSiguientevaloracionUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/mvMensajes").getString("MvSiguientevaloracionDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<MvSiguientevaloracion> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/mvMensajes").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/mvMensajes").getString("PersistenceErrorOccured"));
            }
        }
    }

    public MvSiguientevaloracion getMvSiguientevaloracion(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<MvSiguientevaloracion> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<MvSiguientevaloracion> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = MvSiguientevaloracion.class)
    public static class MvSiguientevaloracionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MvSiguientevaloracionController controller = (MvSiguientevaloracionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mvSiguientevaloracionController");
            return controller.getMvSiguientevaloracion(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof MvSiguientevaloracion) {
                MvSiguientevaloracion o = (MvSiguientevaloracion) object;
                return getStringKey(o.getSigId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), MvSiguientevaloracion.class.getName()});
                return null;
            }
        }

    }

}

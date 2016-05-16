package co.edu.unicauca.gymadmdoc.managedbeans;

import co.edu.unicauca.gymadmdoc.entities.UsuarioMedicamento;
import co.edu.unicauca.gymadmdoc.managedbeans.util.JsfUtil;
import co.edu.unicauca.gymadmdoc.managedbeans.util.PaginationHelper;
import co.edu.unicauca.gymadmdoc.sessionbeans.UsuarioMedicamentoFacade;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("usuarioMedicamentoController")
@SessionScoped
public class UsuarioMedicamentoController implements Serializable {

   private UsuarioMedicamento current;
   private DataModel items = null;
   @EJB
   private co.edu.unicauca.gymadmdoc.sessionbeans.UsuarioMedicamentoFacade ejbFacade;
   private PaginationHelper pagination;
   private int selectedItemIndex;

   public UsuarioMedicamentoController() {
   }

   public UsuarioMedicamento getSelected() {
      if (current == null) {
         current = new UsuarioMedicamento();
         current.setUsuarioMedicamentoPK(new co.edu.unicauca.gymadmdoc.entities.UsuarioMedicamentoPK());
         selectedItemIndex = -1;
      }
      return current;
   }

   private UsuarioMedicamentoFacade getFacade() {
      return ejbFacade;
   }

   public PaginationHelper getPagination() {
      if (pagination == null) {
         pagination = new PaginationHelper(10) {

            @Override
            public int getItemsCount() {
               return getFacade().count();
            }

            @Override
            public DataModel createPageDataModel() {
               return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
            }
         };
      }
      return pagination;
   }

   public String prepareList() {
      recreateModel();
      return "List";
   }

   public String prepareView() {
      current = (UsuarioMedicamento) getItems().getRowData();
      selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
      return "View";
   }

   public String prepareCreate() {
      current = new UsuarioMedicamento();
      current.setUsuarioMedicamentoPK(new co.edu.unicauca.gymadmdoc.entities.UsuarioMedicamentoPK());
      selectedItemIndex = -1;
      return "Create";
   }

   public String create() {
      try {
         getFacade().create(current);
         JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsuarioMedicamentoCreated"));
         return prepareCreate();
      } catch (Exception e) {
         JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
         return null;
      }
   }

   public String prepareEdit() {
      current = (UsuarioMedicamento) getItems().getRowData();
      selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
      return "Edit";
   }

   public String update() {
      try {
         getFacade().edit(current);
         JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsuarioMedicamentoUpdated"));
         return "View";
      } catch (Exception e) {
         JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
         return null;
      }
   }

   public String destroy() {
      current = (UsuarioMedicamento) getItems().getRowData();
      selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
      performDestroy();
      recreatePagination();
      recreateModel();
      return "List";
   }

   public String destroyAndView() {
      performDestroy();
      recreateModel();
      updateCurrentItem();
      if (selectedItemIndex >= 0) {
         return "View";
      } else {
         // all items were removed - go back to list
         recreateModel();
         return "List";
      }
   }

   private void performDestroy() {
      try {
         getFacade().remove(current);
         JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsuarioMedicamentoDeleted"));
      } catch (Exception e) {
         JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
      }
   }

   private void updateCurrentItem() {
      int count = getFacade().count();
      if (selectedItemIndex >= count) {
         // selected index cannot be bigger than number of items:
         selectedItemIndex = count - 1;
         // go to previous page if last page disappeared:
         if (pagination.getPageFirstItem() >= count) {
            pagination.previousPage();
         }
      }
      if (selectedItemIndex >= 0) {
         current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
      }
   }

   public DataModel getItems() {
      if (items == null) {
         items = getPagination().createPageDataModel();
      }
      return items;
   }

   private void recreateModel() {
      items = null;
   }

   private void recreatePagination() {
      pagination = null;
   }

   public String next() {
      getPagination().nextPage();
      recreateModel();
      return "List";
   }

   public String previous() {
      getPagination().previousPage();
      recreateModel();
      return "List";
   }

   public SelectItem[] getItemsAvailableSelectMany() {
      return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
   }

   public SelectItem[] getItemsAvailableSelectOne() {
      return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
   }

   public UsuarioMedicamento getUsuarioMedicamento(co.edu.unicauca.gymadmdoc.entities.UsuarioMedicamentoPK id) {
      return ejbFacade.find(id);
   }

   @FacesConverter(forClass = UsuarioMedicamento.class)
   public static class UsuarioMedicamentoControllerConverter implements Converter {

      private static final String SEPARATOR = "#";
      private static final String SEPARATOR_ESCAPED = "\\#";

      @Override
      public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
         if (value == null || value.length() == 0) {
            return null;
         }
         UsuarioMedicamentoController controller = (UsuarioMedicamentoController) facesContext.getApplication().getELResolver().
                 getValue(facesContext.getELContext(), null, "usuarioMedicamentoController");
         return controller.getUsuarioMedicamento(getKey(value));
      }

      co.edu.unicauca.gymadmdoc.entities.UsuarioMedicamentoPK getKey(String value) {
         co.edu.unicauca.gymadmdoc.entities.UsuarioMedicamentoPK key;
         String values[] = value.split(SEPARATOR_ESCAPED);
         key = new co.edu.unicauca.gymadmdoc.entities.UsuarioMedicamentoPK();
         key.setUsuIdentificacion(Long.parseLong(values[0]));
         key.setMedId(Integer.parseInt(values[1]));
         return key;
      }

      String getStringKey(co.edu.unicauca.gymadmdoc.entities.UsuarioMedicamentoPK value) {
         StringBuilder sb = new StringBuilder();
         sb.append(value.getUsuIdentificacion());
         sb.append(SEPARATOR);
         sb.append(value.getMedId());
         return sb.toString();
      }

      @Override
      public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
         if (object == null) {
            return null;
         }
         if (object instanceof UsuarioMedicamento) {
            UsuarioMedicamento o = (UsuarioMedicamento) object;
            return getStringKey(o.getUsuarioMedicamentoPK());
         } else {
            throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + UsuarioMedicamento.class.getName());
         }
      }

   }

}
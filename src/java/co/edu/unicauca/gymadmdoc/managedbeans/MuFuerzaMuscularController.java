package co.edu.unicauca.gymadmdoc.managedbeans;

import co.edu.unicauca.gymadmdoc.entities.MuFuerzaMuscular;
import co.edu.unicauca.gymadmdoc.managedbeans.util.JsfUtil;
import co.edu.unicauca.gymadmdoc.managedbeans.util.PaginationHelper;
import co.edu.unicauca.gymadmdoc.sessionbeans.MuFuerzaMuscularFacade;

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

@Named("muFuerzaMuscularController")
@SessionScoped
public class MuFuerzaMuscularController implements Serializable {

   private MuFuerzaMuscular current;
   private DataModel items = null;
   @EJB
   private co.edu.unicauca.gymadmdoc.sessionbeans.MuFuerzaMuscularFacade ejbFacade;
   private PaginationHelper pagination;
   private int selectedItemIndex;

   public MuFuerzaMuscularController() {
   }

   public MuFuerzaMuscular getSelected() {
      if (current == null) {
         current = new MuFuerzaMuscular();
         selectedItemIndex = -1;
      }
      return current;
   }

   private MuFuerzaMuscularFacade getFacade() {
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
      current = (MuFuerzaMuscular) getItems().getRowData();
      selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
      return "View";
   }

   public String prepareCreate() {
      current = new MuFuerzaMuscular();
      selectedItemIndex = -1;
      return "Create";
   }

   public String create() {
      try {
         getFacade().create(current);
         JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("MuFuerzaMuscularCreated"));
         return prepareCreate();
      } catch (Exception e) {
         JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
         return null;
      }
   }

   public String prepareEdit() {
      current = (MuFuerzaMuscular) getItems().getRowData();
      selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
      return "Edit";
   }

   public String update() {
      try {
         getFacade().edit(current);
         JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("MuFuerzaMuscularUpdated"));
         return "View";
      } catch (Exception e) {
         JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
         return null;
      }
   }

   public String destroy() {
      current = (MuFuerzaMuscular) getItems().getRowData();
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
         JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("MuFuerzaMuscularDeleted"));
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

   public MuFuerzaMuscular getMuFuerzaMuscular(java.lang.Integer id) {
      return ejbFacade.find(id);
   }

   @FacesConverter(forClass = MuFuerzaMuscular.class)
   public static class MuFuerzaMuscularControllerConverter implements Converter {

      @Override
      public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
         if (value == null || value.length() == 0) {
            return null;
         }
         MuFuerzaMuscularController controller = (MuFuerzaMuscularController) facesContext.getApplication().getELResolver().
                 getValue(facesContext.getELContext(), null, "muFuerzaMuscularController");
         return controller.getMuFuerzaMuscular(getKey(value));
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
         if (object instanceof MuFuerzaMuscular) {
            MuFuerzaMuscular o = (MuFuerzaMuscular) object;
            return getStringKey(o.getFumusId());
         } else {
            throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + MuFuerzaMuscular.class.getName());
         }
      }

   }

}
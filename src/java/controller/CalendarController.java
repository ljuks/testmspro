package controller;


import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
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
import net.sf.mpxj.Duration; 
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectCalendar; 
import net.sf.mpxj.ProjectFile; 
import net.sf.mpxj.Relation; 
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment; 
import net.sf.mpxj.Task;
import net.sf.mpxj.mpp.MPPReader; 
import net.sf.mpxj.mpx.MPXReader; 
import net.sf.mpxj.mspdi.schema.TimephasedDataType; 
import net.sf.mpxj.planner.PlannerReader; 
import net.sf.mpxj.reader.AbstractProjectReader;
import net.sf.mpxj.reader.ProjectReader;

@Named("calendarController")
@SessionScoped
public class CalendarController implements Serializable {

    @EJB
    private controller.CalendarFacade ejbFacade;
    private List<Calendar> items = null;
    private Calendar selected;

    public CalendarController() {
    }

    public Calendar getSelected() {
        return selected;
    }

    public void setSelected(Calendar selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private CalendarFacade getFacade() {
        return ejbFacade;
    }
public void test()   {
    Date st ,fi;  
    int ii;
        try {
            ProjectReader reader = new MPPReader ();
            //           InputStream in = new FileInputStream("D:/Z01074-G.mpp");
            ProjectFile pr=reader.read("D:/Z.mpp");
            JsfUtil.addSuccessMessage("111"); 
            //     try {
            // pr = reader.read("D:/test.mpp");
            
            JsfUtil.addSuccessMessage("111");
            
/*            for (Task task : pr.getAllTasks()) {
                System.out.println("Task: " + task.getName() + " ID=" + task.getID() + " Unique ID=" + task.getUniqueID());
            }
            
            for (Resource resource : pr.getAllResources())
                System.out.println("Resource: " + resource.getName() + " (Unique ID=" + resource.getUniqueID() + ")");*/
 for (Task task : pr.getAllTasks())
 { 
    System.out.println("Assignments for task " + task.getName() + ":");
 st = task.getStart();
//task.getNumber(1);
// getNumber(4) -hours
// gettext 1 - Z01074-G, 2 - Корпуса редукторов - 20 шт, 3- LNS33552X902-OP6+OP9-W, 4 - 181\1, 5-018,6-участок 018
 for (ii=1; ii<20; ii++){
 if (task.getNumber(ii) != null && task.getNumber(ii).doubleValue()>  0.0)
  System.out.println("Assignments for task " + task.getName() + ":");   
 }
 //fi = task.getText(0)
 //st= (Date) task.m_array[0];
    for (ResourceAssignment assignment : task.getResourceAssignments())
    {
       Resource resource = assignment.getResource();
       String resourceName;

       if (resource == null)
       {
          resourceName = "(null resource)";
       }
       else
       {
          resourceName = resource.getName();
       }

       System.out.println("   " + resourceName);
    }
 }


        } catch (MPXJException ex) {
            Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
     
    }
    public Calendar prepareCreate() {
        selected = new Calendar();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CalendarCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("CalendarUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("CalendarDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Calendar> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Calendar getCalendar(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Calendar> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Calendar> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Calendar.class)
    public static class CalendarControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CalendarController controller = (CalendarController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "calendarController");
            return controller.getCalendar(getKey(value));
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
            if (object instanceof Calendar) {
                Calendar o = (Calendar) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Calendar.class.getName()});
                return null;
            }
        }

    }

}

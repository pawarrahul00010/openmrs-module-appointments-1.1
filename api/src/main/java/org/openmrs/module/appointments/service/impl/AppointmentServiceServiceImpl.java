package org.openmrs.module.appointments.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.module.appointments.dao.AppointmentServiceDao;
import org.openmrs.module.appointments.model.*;
import org.openmrs.module.appointments.service.AppointmentServiceService;
import org.openmrs.module.appointments.service.AppointmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
public class AppointmentServiceServiceImpl implements AppointmentServiceService {

    AppointmentServiceDao appointmentServiceDao;

    AppointmentsService appointmentsService;

    public void setAppointmentServiceDao(AppointmentServiceDao appointmentServiceDao) {
        this.appointmentServiceDao = appointmentServiceDao;
    }

    public void setAppointmentsService(AppointmentsService appointmentsService) {
        this.appointmentsService = appointmentsService;
    }

    @Override
    public AppointmentService save(AppointmentService appointmentService) {
        AppointmentService service = appointmentServiceDao.getNonVoidedAppointmentServiceByName(appointmentService.getName());
        if(service != null && !service.getUuid().equals(appointmentService.getUuid())) {
            throw new RuntimeException("The service '" + appointmentService.getName() + "' is already present");
        }
        return appointmentServiceDao.save(appointmentService);
    }

    @Override
    public List<AppointmentService> getAllAppointmentServices(boolean includeVoided) {
        List<AppointmentService> appointmentServices = appointmentServiceDao.getAllAppointmentServices(includeVoided);
        return appointmentServices;
    }

    @Override
    public AppointmentService getAppointmentServiceByUuid(String uuid) {
        AppointmentService appointmentService = appointmentServiceDao.getAppointmentServiceByUuid(uuid);
        return appointmentService;
    }

    @Override
    public AppointmentService voidAppointmentService(AppointmentService appointmentService, String voidReason) {
        List<Appointment> allFutureAppointmentsForService = appointmentsService.getAllFutureAppointmentsForService(appointmentService);
        if (allFutureAppointmentsForService.size() > 0) {
            throw new RuntimeException("Please cancel all future appointments for this service to proceed. After deleting this service, you will not be able to see any appointments for it");
        }
        setVoidInfoForAppointmentService(appointmentService, voidReason);
        return appointmentServiceDao.save(appointmentService);
    }

    @Override
    public AppointmentServiceType getAppointmentServiceTypeByUuid(String serviceTypeUuid) {
        return appointmentServiceDao.getAppointmentServiceTypeByUuid(serviceTypeUuid);
    }

    @Override
    public Integer calculateCurrentLoad(AppointmentService appointmentService, Date startDateTime, Date endDateTime) {
        AppointmentStatus[] includeStatus = new AppointmentStatus[]{AppointmentStatus.CheckedIn, AppointmentStatus.Completed, AppointmentStatus.Scheduled};
        List<Appointment> appointmentsForService = appointmentsService
                .getAppointmentsForService(appointmentService, startDateTime, endDateTime, Arrays.asList(includeStatus));
        return appointmentsForService.size();
    }

    private void setVoidInfoForAppointmentService(AppointmentService appointmentService, String voidReason) {
        setVoidInfoForService(appointmentService, voidReason);
        setVoidInfoForWeeklyAvailability(appointmentService, voidReason);
        setVoidInfoForServiceTypes(appointmentService, voidReason);
    }

    private void setVoidInfoForService(AppointmentService appointmentService, String voidReason) {
        appointmentService.setVoided(true);
        appointmentService.setDateVoided(new Date());
        appointmentService.setVoidedBy(Context.getAuthenticatedUser());
        appointmentService.setVoidReason(voidReason);
    }

    private void setVoidInfoForServiceTypes(AppointmentService appointmentService, String voidReason) {
        for (AppointmentServiceType appointmentServiceType : appointmentService.getServiceTypes()) {
            appointmentServiceType.setVoided(true);
            appointmentServiceType.setDateVoided(new Date());
            appointmentServiceType.setVoidedBy(Context.getAuthenticatedUser());
            appointmentServiceType.setVoidReason(voidReason);
        }
    }

    private void setVoidInfoForWeeklyAvailability(AppointmentService appointmentService, String voidReason) {
        for (ServiceWeeklyAvailability serviceWeeklyAvailability : appointmentService.getWeeklyAvailability()) {
            serviceWeeklyAvailability.setVoided(true);
            serviceWeeklyAvailability.setDateVoided(new Date());
            serviceWeeklyAvailability.setVoidedBy(Context.getAuthenticatedUser());
            serviceWeeklyAvailability.setVoidReason(voidReason);
        }
    }
    
    @Override
    public AppointmentServiceType getAppointmentServiceTypeByName(int serviceId, String serviceTypeName) {
        return appointmentServiceDao.getAppointmentServiceTypeByName(serviceId , serviceTypeName);
    }

}

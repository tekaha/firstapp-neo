package de.adwizor.cloud.sdk.tutorial;

import com.google.gson.Gson;
import com.sap.cloud.sdk.cloudplatform.auditlog.*;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;

import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultBusinessPartnerService;

@WebServlet("/businesspartners")
public class BusinessPartnerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = CloudLoggerFactory.getLogger(BusinessPartnerServlet.class);

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String businessPartnerId = request.getParameter("id");

        // 1a. Create AccessedAttribute Object for more insights on operation attempt of an attribute
        AccessedAttribute attemptAttributes = new AccessedAttribute(
                "BusinessPartner.CreationDate",
                AccessedAttribute.Operation.READ
        );

        // 1b. Create AuditedDataObject
        AuditedDataObject auditedDataObject = new AuditedDataObject(
                "BusinessPartner",
                businessPartnerId
        );

        // 1c. Create AuditedDataSubject
        AuditedDataSubject auditedDataSubject = new AuditedDataSubject(
                "BusinessPartner",
                "Read Operation"
        );

        // 2. Log AccessedAttribute Object
        AuditLogger.logDataReadAttempt(
                AccessRequester.UNKNOWN,
                auditedDataObject,
                auditedDataSubject,
                Collections.singletonList(attemptAttributes)
        );

        try {
            final BusinessPartner businessPartner =
                    new DefaultBusinessPartnerService()
                            .getBusinessPartnerByKey(businessPartnerId)
                            .select(BusinessPartner.BUSINESS_PARTNER,
                                    BusinessPartner.LAST_NAME,
                                    BusinessPartner.FIRST_NAME,
                                    BusinessPartner.IS_MALE,
                                    BusinessPartner.IS_FEMALE,
                                    BusinessPartner.CREATION_DATE)
                            .execute();

            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(businessPartner));

            // 3. Create AccessedAttribute Object to log output of operation of an attribute
            AccessedAttribute auditableValueAccessedAttribute = new AccessedAttribute(
                    "BusinessPartner.CreationDate",
                    AccessedAttribute.Operation.READ,
                    "Creation date of business partner",
                    businessPartner.getCreationDate(),
                    null,
                    true
            );
            // 4a. Log AccessedAttribute Object
            AuditLogger.logDataRead(
                    AccessRequester.UNKNOWN,
                    auditedDataObject,
                    auditedDataSubject,
                    Collections.singleton(auditableValueAccessedAttribute),
                    null
            );


        } catch (final ODataException e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());

            AccessedAttribute failedAttemptAttributes = new AccessedAttribute(
                    "BusinessPartner.CreationDate",
                    AccessedAttribute.Operation.READ,
                    "Creation date of business partner",
                    null,
                    null,
                    false
            );
            // 4b. Log AccessedAttribute Object
            AuditLogger.logDataRead(
                    AccessRequester.UNKNOWN,
                    auditedDataObject,
                    auditedDataSubject,
                    Collections.singleton(failedAttemptAttributes),
                    null
            );
        }
    }
}
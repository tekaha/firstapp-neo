package de.adwizor.cloud.sdk.tutorial;

import com.google.gson.Gson;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;

import com.sap.cloud.sdk.s4hana.datamodel.odata.helper.Order;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultBusinessPartnerService;

@WebServlet("/businesspartners")
public class BusinessPartnerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // 1. Get logger for our servlet
    private static final Logger logger = CloudLoggerFactory.getLogger(BusinessPartnerServlet.class);

    private static final String CATEGORY_PERSON = "1";

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        // 2. Log event providing INFO level, info is moderate
        logger.info("Attempt to SELECT business partners");

        try {
            final List<BusinessPartner> businessPartners =
                    new DefaultBusinessPartnerService()
                            .getAllBusinessPartner()
                            .select(BusinessPartner.BUSINESS_PARTNER,
                                    BusinessPartner.LAST_NAME,
                                    BusinessPartner.FIRST_NAME,
                                    BusinessPartner.IS_MALE,
                                    BusinessPartner.IS_FEMALE,
                                    BusinessPartner.CREATION_DATE)
                            .filter(BusinessPartner.BUSINESS_PARTNER_CATEGORY.eq(CATEGORY_PERSON))
                            .orderBy(BusinessPartner.LAST_NAME, Order.ASC)
                            .execute();

            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(businessPartners));

            // 3a. Log event including basic information providing INFO level
            logger.info("Succeeded to SELECT {} business partners", businessPartners.size());

        } catch (final ODataException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());

            // 3b. Log event including exception providing ERROR level
            logger.error("Failed to SELECT business partners", e);
        }
    }
}
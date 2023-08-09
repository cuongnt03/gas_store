/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.se1715.group4.gasstore.models.client;

import com.se1715.group4.gasstore.dao.DAOCustomer;
import com.se1715.group4.gasstore.dao.DAOHeaderFooter;
import com.se1715.group4.gasstore.dao.DAOProduct;
import com.se1715.group4.gasstore.dto.Cart;
import com.se1715.group4.gasstore.dto.Customer;
import com.se1715.group4.gasstore.dto.Footer;
import com.se1715.group4.gasstore.dto.Header;
import com.se1715.group4.gasstore.dto.Item;
import com.se1715.group4.gasstore.dto.Product;
import com.se1715.group4.gasstore.util.AES;
import com.se1715.group4.gasstore.util.SendMail;
import com.se1715.group4.gasstore.util.Validation;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ADMIN
 */
public class ForgotPasswordClientServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ForgotPasswordClientServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ForgotPasswordClientServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DAOHeaderFooter daoHeaderFooter = new DAOHeaderFooter();
        ArrayList<Footer> footers = daoHeaderFooter.getFooters();
        request.setAttribute("footers", footers);
        ArrayList<Header> headers = daoHeaderFooter.getHeaders();
        request.setAttribute("headers", headers);
        request.getRequestDispatcher("inputemail.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        DAOCustomer daoCustomers = new DAOCustomer();
        
        DAOHeaderFooter daoHeaderFooter = new DAOHeaderFooter();
        ArrayList<Footer> footers = daoHeaderFooter.getFooters();
        request.setAttribute("footers", footers);
        ArrayList<Header> headers = daoHeaderFooter.getHeaders();
        request.setAttribute("headers", headers);

        
        String email = request.getParameter("email");
        if (email == null) {
            request.setAttribute("error", "Email không được để trống!!!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
        if(!Validation.validateEmail(email)){
            request.setAttribute("error", "Invalid Email!!!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
        Customer cus = daoCustomers.getCustomerByEmail(email);
        if (cus != null) {
            try {
                SendMail send = new SendMail();
                String otp = send.generateOTP();
                
                send.sendOTP(cus.getEmail(), otp);
                request.setAttribute("userName", cus.getUserName());
                request.setAttribute("otpSend", AES.encrypt(otp));
                request.setAttribute("email", cus.getEmail());
                request.getRequestDispatcher("inputotp.jsp").forward(request, response);
            } catch (Exception ex) {
                Logger.getLogger(ForgotPasswordClientServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            request.setAttribute("error", "Email bạn vùa nhập không tồn tại!!!");
            request.getRequestDispatcher("inputemail.jsp").forward(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

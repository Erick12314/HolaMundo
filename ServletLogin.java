package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import daofactory.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Model.BeanPais;
import Model.BeanPerfil;
import Model.BeanPerson_perfil;
import Model.BeanReg_cred;
import Model.BeanSubCategoria;
import dao.interfaces.I_Categoria;
import dao.interfaces.I_Pais;
import dao.interfaces.I_Perfil;
import dao.interfaces.I_Sub_cat;
import dao.interfaces.I_Usuario;
import dao.mysql.MySql_Sub_catDao;

/**
 * Servlet implementation class ServletLogin
 */
@WebServlet("/Login")
public class ServletLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out=response.getWriter();
		HttpSession session=request.getSession();
		BeanReg_cred user=(BeanReg_cred) session.getAttribute("usuario");
		
		if(user!=null){
			try {
				DAOFactory dao = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
				if(user.getTipoReg().equals("CLIENTE")){
					I_Perfil perfildao=dao.getPerfilDao();
					I_Usuario usuariodao=dao.getUsuarioDao();
					Vector<BeanPerson_perfil> perfiles_usuario=usuariodao.perfiles_usuario(user.getPersona().getIdPersona());
					request.setAttribute("perfiles_usuario", perfiles_usuario);
					
					Vector<BeanPerfil> perfiles=perfildao.listarPerfiles();
					request.setAttribute("perfiles", perfiles);
					
					request.getRequestDispatcher("/Cliente/perfiles.jsp").forward(request, response);
				}else if(user.getTipoReg().equals("ADMINISTRADOR")){
					I_Sub_cat daoa = new MySql_Sub_catDao();
					List<BeanSubCategoria> sub_categorias = daoa.listar();
					
					request.setAttribute("sub_categorias", sub_categorias);
					
					
					DAOFactory daoav = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
					I_Categoria categoriadao=daoav.getCategoriDao();
					request.setAttribute("categorias", categoriadao.Listarcategorias());
					request.getRequestDispatcher("/Administrador/sub-categorias.jsp").forward(request, response);
				}
			} catch (Exception e) {
				out.println(e.getMessage());
			}
		}else{
			try {
				DAOFactory dao = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
				I_Pais paisdao = dao.getPaisDao();
				Vector<BeanPais>paises=paisdao.listarPaises();
				request.setAttribute("paises", paises);
				request.getRequestDispatcher("/login.jsp").forward(request, response);
			} catch (Exception e) {
				out.println(e.getMessage());
			}
			
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out=response.getWriter();
		try{
			DAOFactory dao = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
			String uss=request.getParameter("user").replaceAll("'or'", "");
			String pass= request.getParameter("pass").replaceAll("'or'", "");
			if(uss.equals("")||pass.equals("")){
				I_Pais paisdao = dao.getPaisDao();
				Vector<BeanPais>paises=paisdao.listarPaises();
				request.setAttribute("paises", paises);
				request.setAttribute("mensaje", "Ingrese su usuario y clave");
				request.getRequestDispatcher("/login.jsp").forward(request, response);
			}else{
				
				I_Usuario usuariodao = dao.getUsuarioDao();
				
				BeanReg_cred ses=usuariodao.validar(uss,pass);
				if(ses!=null){
					HttpSession session= request.getSession();
					session.setAttribute("usuario", ses);
					session.setMaxInactiveInterval(-1);
					if(ses.getTipoReg().equals("CLIENTE")){
						session.removeAttribute("idPerfilUsuario");
						I_Perfil perfildao=dao.getPerfilDao();
						usuariodao=dao.getUsuarioDao();
						Vector<BeanPerson_perfil> perfiles_usuario=usuariodao.perfiles_usuario(ses.getPersona().getIdPersona());
						request.setAttribute("perfiles_usuario", perfiles_usuario);
						Vector<BeanPerfil> perfiles=perfildao.listarPerfiles();
						request.setAttribute("perfiles", perfiles);
						request.getRequestDispatcher("/Cliente/perfiles.jsp").forward(request, response);
					}else if(ses.getTipoReg().equals("ADMINISTRADOR")){
						I_Sub_cat daoa = new MySql_Sub_catDao();
						List<BeanSubCategoria> sub_categorias = daoa.listar();
						
						request.setAttribute("sub_categorias", sub_categorias);
						
						
						DAOFactory daoav = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
						I_Categoria categoriadao=daoav.getCategoriDao();
						request.setAttribute("categorias", categoriadao.Listarcategorias());
						request.getRequestDispatcher("/Administrador/sub-categorias.jsp").forward(request, response);
					}
				}else{
					I_Pais paisdao = dao.getPaisDao();
					Vector<BeanPais>paises=paisdao.listarPaises();
					request.setAttribute("paises", paises);
					request.setAttribute("mensaje", "Datos Incorrectos");
					request.getRequestDispatcher("/login.jsp").forward(request, response);
				}
			}
		}catch (Exception e) {
			out.print(e.getMessage());
		}	
	}

}

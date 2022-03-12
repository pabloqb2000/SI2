/**
 * Pr&aacute;ctricas de Sistemas Inform&aacute;ticos II
 * VisaCancelacionJMSBean.java
 */

package ssii2.visa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.ActivationConfigProperty;
import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.JMSException;
import javax.annotation.Resource;
import java.util.logging.Logger;

/**
 * @author jaime
 */
@MessageDriven(mappedName = "jms/VisaPagosQueue")
public class VisaCancelacionJMSBean extends DBTester implements MessageListener {
  static final Logger logger = Logger.getLogger("VisaCancelacionJMSBean");
  @Resource
  private MessageDrivenContext mdc;

  private static final String UPDATE_CANCELA_QRY = "update pago set codRespuesta=999 where idautorizacion=?";
  private static final String UPDATE_SALDO = "update tarjeta set saldo=saldo+(select importe from pago where idautorizacion=?)" +
                                              " where numerotarjeta=(select numerotarjeta form pago where idautorizacion=?)";

  public VisaCancelacionJMSBean() {
  }

  // TODO : Método onMessage de ejemplo
  // Modificarlo para ejecutar el UPDATE definido más arriba,
  // asignando el idAutorizacion a lo recibido por el mensaje
  // Para ello conecte a la BD, prepareStatement() y ejecute correctamente
  // la actualización
  public void onMessage(Message inMessage) {
      TextMessage msg = null;
      Connection con = null;
      PreparedStatement pstmt = null;
      boolean flag = false;

      try {
          if (inMessage instanceof TextMessage) {
              msg = (TextMessage) inMessage;
              logger.info("MESSAGE BEAN: Message received: " + msg.getText());

              int idAutorizacion =  Integer.parseInt(msg.getText());

              con = getConnection();

              String updResp = UPDATE_CANCELA_QRY;
              logger.info(updResp);
              pstmt = con.prepareStatement(updResp);
              pstmt.setInt(1, idAutorizacion);
              flag = false;
              if(!pstmt.execute() && pstmt.getUpdateCount() == 1){
                  flag = true;
              }

              if(flag){
                  String updSaldo = UPDATE_SALDO;
                  logger.info(updSaldo);
                  pstmt = con.prepareStatement(updSaldo);
                  pstmt.setInt(1, idAutorizacion);
                  pstmt.setInt(2, idAutorizacion);
                  flag = false;
                  if(!pstmt.execute() && pstmt.getUpdateCount() == 1){
                    flag = true;
                }
              }
          } else {
              logger.warning(
                      "Message of wrong type: "
                      + inMessage.getClass().getName());
          }
      } catch (JMSException e) {
          e.printStackTrace();
          mdc.setRollbackOnly();
      } catch (Throwable te) {
          te.printStackTrace();
      }finally{
          try{
              if(pstmt != null){
                  pstmt.close();
                  pstmt = null;
              }
              if(con != null){
                  closeConnection(con);
                  con = null;
              }
          }catch(SQLException e){
          }
      }
  }


}

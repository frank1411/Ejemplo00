PROCEDURE CONSULTA_DET_TRANS_NOM_PRO (
 v_tipo_producto IN VARCHAR2,                   
 v_f_inicio IN VARCHAR2,
 v_f_fin IN VARCHAR2,
 P_OUT_DATA OUT SYS_REFCURSOR,
 COD_RET OUT VARCHAR2,
 DE_RET OUT VARCHAR2
) AS
  -- Excepciones personalizadas
  valor_null EXCEPTION;
  fecha_invalida EXCEPTION;
  inicio_mayor_que_fin EXCEPTION;
  no_afiliacion EXCEPTION;
  no_reporte EXCEPTION;
BEGIN
    OPEN P_OUT_DATA FOR SELECT * FROM dual; 

  -- Verificar datos de entrada nulos
  IF v_tipo_producto IS NULL OR v_tipo_producto = '' OR
     v_f_inicio IS NULL OR v_f_inicio = '' OR
     v_f_fin IS NULL OR v_f_fin = '' THEN
    COD_RET := '1001';  -- Campos nulos
    DE_RET := 'Todos los campos de entrada deben estar poblados';
    RAISE valor_null;
  END IF;

  -- Verificar fecha fin
  IF TO_DATE(v_f_fin, 'dd/mm/yyyy') > SYSDATE OR TO_DATE(v_f_fin, 'dd/mm/yyyy') < ADD_MONTHS(SYSDATE, -12) THEN
    COD_RET := '1002';  -- Fecha fin inválida
    DE_RET := 'La fecha fin no puede ser mayor a la fecha actual ni menor a la fecha de hace un año';
    RAISE fecha_invalida;
  END IF;

  -- Verificar fecha inicio
  IF TO_DATE(v_f_inicio, 'dd/mm/yyyy') > SYSDATE OR TO_DATE(v_f_inicio, 'dd/mm/yyyy') < ADD_MONTHS(SYSDATE, -12) THEN
    COD_RET := '1003';  -- Fecha inicio inválida
    DE_RET := 'La fecha inicio no puede ser mayor a la fecha actual ni menor a la fecha de hace un año';
    RAISE fecha_invalida;
  END IF;

  -- Verificar rango de fechas
  IF TO_DATE(v_f_inicio, 'dd/mm/yyyy') > TO_DATE(v_f_fin, 'dd/mm/yyyy') THEN
    COD_RET := '1004';  -- Rango de fechas inválido
    DE_RET := 'La fecha de inicio no puede ser mayor que la fecha final';
    RAISE inicio_mayor_que_fin;
  END IF;

  -- Verificar tipo de producto
  IF (v_tipo_producto <> 'PAYROLL' and v_tipo_producto <> 'PROVIDER') THEN
    COD_RET := '1005';  -- Tipo de producto inválido
    DE_RET := 'El reporte de comisiones aplica solo para los productos NOMINA y PROVEEDORES';
    RAISE no_reporte;  
  END IF;

 OPEN P_OUT_DATA FOR

WITH ord AS (
  SELECT 
    do.id_orden, 
    do.id_debits,
    do.CUSTOMER_ID 
  FROM  
    debits do  
  WHERE 
    do.process_date BETWEEN to_date(v_f_inicio,'dd/mm/yyyy') AND to_date(v_f_fin,'dd/mm/yyyy')
    AND do.status = 3  
    AND do.business_status_code = '00' 
    AND do.payment_type = DECODE(v_tipo_producto,'PAYROLL',10,'PROVIDER',40)
)
SELECT 
  Cli.customer_id AS ruf,
  cli.num_negotiation AS negociacion,
  tr.volumenLotes AS volumenLotes,
  cli.cant_lotes AS cli,
  Tr.Monto_Abonado AS Monto_Abonado,
  Tr.Comision AS Comision_cobrada,
  cli.canal_bdvempresa AS Carga_BDVenLinea,
  Cli.canal_otros AS Carga_Otros_Canales, 
  tr.total_trans AS Total_Transacciones,
  Rev.MontoAbonadoRev AS MontoAbonadoRev,
  Rev.CantidadReverso AS CantidadReverso
FROM
  (
    SELECT 
      do.customer_id,
      COUNT(DISTINCT do.id_orden) AS volumenLotes,
      SUM(do.monto_actualizado) AS monto_abonado,
      SUM(do.TOTAL_CREDITOS_ACEPTADOS) AS Total_trans,
      SUM(
        CASE
          WHEN d.business_status_code = '00' THEN d.AMOUNT
          ELSE 0
        END
      ) AS comision
    FROM 
      ord o 
      INNER JOIN pcp.debits do ON o.id_orden = do.id_orden AND o.id_debits = do.id_debits
      LEFT JOIN pcp.debits d ON d.id_debits_root = do.id_debits AND d.id_orden = d.id_orden
    WHERE  
      do.process_date BETWEEN to_date(v_f_inicio,'dd/mm/yyyy') AND to_date(v_f_fin,'dd/mm/yyyy') 
      AND d.process_date BETWEEN to_date(v_f_inicio,'dd/mm/yyyy') AND to_date(v_f_fin,'dd/mm/yyyy') 
      AND d.payment_type = 60
    GROUP BY 
      do.customer_id
  ) TR  
  INNER JOIN 
  (
    SELECT 
      O.CUSTOMER_ID,
      b.num_negotiation,
      COUNT(o.id_orden) AS cant_lotes,
      SUM(
        CASE 
          WHEN OD.SEND_CHANNEL = 1 THEN 1
          WHEN OD.SEND_CHANNEL = 2 THEN 1 
          ELSE 0
        END
      ) AS canal_BDVEMPRESA,
      SUM(
        CASE 
          WHEN OD.SEND_CHANNEL = 1 THEN 0
          WHEN OD.SEND_CHANNEL = 2 THEN 0 
          ELSE 1
        END
      ) AS canal_otros
    FROM 
      (SELECT DISTINCT id_orden, customer_id FROM ord) o
      INNER JOIN 
      (
        SELECT 
          id_orden,
          DECODE(od.send_channel,2,1,9,1,10,14,od.send_channel) AS channel, 
          send_channel 
        FROM 
          orden_detail od 
        WHERE 
          prov_id_orden_root IS NULL AND send_channel <> 36
      ) od  ON  o.id_orden = od.id_orden
      LEFT JOIN 
      (
        SELECT 
          customer_id, 
          b.channel, 
          MIN(num_negotiation) AS num_negotiation 
        FROM 
          business_rules b 
        WHERE 
          status = 'A' AND b.type_rule = v_tipo_producto 
        GROUP BY 
          customer_id, channel, status
      ) b ON b.customer_id = o.customer_id AND od.channel = b.channel  
    GROUP BY 
      O.customer_id, num_negotiation
  ) CLI ON tr.customer_id = cli.customer_id 
  LEFT JOIN        
  (
    SELECT 
      o.customer_id, 
      SUM(d.monto_actualizado) AS montoAbonadoRev, 
      COUNT(o.id_orden) AS CantidadReverso
    FROM 
      debits d
      INNER JOIN orden o ON o.id_orden = d.id_orden
    WHERE
      d.payment_type = 90 
      AND d.STATUS = 3 
      AND d.business_status_code = 00 
      AND o.product_name = v_tipo_producto 
      AND d.process_date BETWEEN to_date(v_f_inicio,'dd/mm/yyyy') AND to_date(v_f_fin,'dd/mm/yyyy')
      AND o.date_value BETWEEN  to_date(v_f_inicio,'dd/mm/yyyy') AND to_date(v_f_fin,'dd/mm/yyyy')
      GROUP BY o.CUSTOMER_ID) rev ON cli.customer_id = rev.customer_id;

 -- Asignar valores de éxito
 COD_RET := '1000';  -- Código de éxito
 DE_RET := 'Consulta ejecutada exitosamente';

EXCEPTION
  WHEN valor_null THEN
    -- 1001 ya está asignado en la validación
    NULL;
  WHEN fecha_invalida THEN
    -- 1002 o 1003 ya están asignados en las validaciones
    NULL;
  WHEN inicio_mayor_que_fin THEN
    -- 1004 ya está asignado en la validación
    NULL;
  WHEN no_reporte THEN
    -- 1005 ya está asignado en la validación
    NULL;
  WHEN OTHERS THEN
    COD_RET := '1999';  -- Código genérico para errores no manejados
    DE_RET := 'Error no controlado: ' || SQLERRM;
END CONSULTA_DET_TRANS_NOM_PRO;
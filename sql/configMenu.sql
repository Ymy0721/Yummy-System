use vue_db;
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('AI模型配置', '2000', '1', 'config', 'patentSys/config/index', 1, 0, 'C', '0', '0', 'patentSys:config:list', '#', 'admin', sysdate(), '', null, 'AI模型配置菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('AI模型配置查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'patentSys:config:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('AI模型配置新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'patentSys:config:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('AI模型配置修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'patentSys:config:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('AI模型配置删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'patentSys:config:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('AI模型配置导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'patentSys:config:export',       '#', 'admin', sysdate(), '', null, '');
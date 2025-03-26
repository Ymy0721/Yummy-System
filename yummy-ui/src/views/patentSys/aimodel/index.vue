<template>
    <div class="app-container">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="模型名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入模型名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="模型代码" prop="model">
          <el-input
            v-model="queryParams.model"
            placeholder="请输入模型代码"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="API base URL" prop="baseUrl">
          <el-input
            v-model="queryParams.baseUrl"
            placeholder="请输入API base URL"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="API key" prop="apiKey">
          <el-input
            v-model="queryParams.apiKey"
            placeholder="请输入API key"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="最大token数量" prop="maxTokens">
          <el-input
            v-model="queryParams.maxTokens"
            placeholder="请输入最大token数量"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="温度参数" prop="temperature">
          <el-input
            v-model="queryParams.temperature"
            placeholder="请输入温度参数"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="是否为默认模型(0否 1是)" prop="isDefault">
          <el-input
            v-model="queryParams.isDefault"
            placeholder="请输入是否为默认模型(0否 1是)"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
  
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['patentSys:aimodel:add']"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="el-icon-edit"
            size="mini"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['patentSys:aimodel:edit']"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['patentSys:aimodel:remove']"
          >删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['patentSys:aimodel:export']"
          >导出</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
  
      <el-table v-loading="loading" :data="aimodelList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="配置ID" align="center" prop="id" />
        <el-table-column label="模型名称" align="center" prop="name" />
        <el-table-column label="模型代码" align="center" prop="model" />
        <el-table-column label="API base URL" align="center" prop="baseUrl" />
        <el-table-column label="API key" align="center" prop="apiKey" />
        <el-table-column label="最大token数量" align="center" prop="maxTokens" />
        <el-table-column label="温度参数" align="center" prop="temperature" />
        <el-table-column label="是否为默认模型(0否 1是)" align="center" prop="isDefault" />
        <el-table-column label="状态" align="center" prop="status" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['patentSys:aimodel:edit']"
            >修改</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['patentSys:aimodel:remove']"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
  
      <!-- 添加或修改AI模型配置对话框 -->
      <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
        <el-form ref="form" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="模型名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入模型名称" />
          </el-form-item>
          <el-form-item label="模型代码" prop="model">
            <el-input v-model="form.model" placeholder="请输入模型代码" />
          </el-form-item>
          <el-form-item label="API base URL" prop="baseUrl">
            <el-input v-model="form.baseUrl" placeholder="请输入API base URL" />
          </el-form-item>
          <el-form-item label="API key" prop="apiKey">
            <el-input v-model="form.apiKey" placeholder="请输入API key" />
          </el-form-item>
          <el-form-item label="最大token数量" prop="maxTokens">
            <el-input v-model="form.maxTokens" placeholder="请输入最大token数量" />
          </el-form-item>
          <el-form-item label="温度参数" prop="temperature">
            <el-input v-model="form.temperature" placeholder="请输入温度参数" />
          </el-form-item>
          <el-form-item label="是否为默认模型(0否 1是)" prop="isDefault">
            <el-input v-model="form.isDefault" placeholder="请输入是否为默认模型(0否 1是)" />
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-dialog>
    </div>
  </template>
  
  <script>
  import { listAiModel, getAiModel, delAiModel, addAiModel, updateAiModel } from "@/api/patentSys/aimodel";
  
  export default {
    name: "Aimodel",
    data() {
      return {
        // 遮罩层
        loading: true,
        // 选中数组
        ids: [],
        // 非单个禁用
        single: true,
        // 非多个禁用
        multiple: true,
        // 显示搜索条件
        showSearch: true,
        // 总条数
        total: 0,
        // AI模型配置表格数据
        aimodelList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          name: null,
          model: null,
          baseUrl: null,
          apiKey: null,
          maxTokens: null,
          temperature: null,
          isDefault: null,
          status: null,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          name: [
            { required: true, message: "模型名称不能为空", trigger: "blur" }
          ],
          model: [
            { required: true, message: "模型代码不能为空", trigger: "blur" }
          ],
          baseUrl: [
            { required: true, message: "API base URL不能为空", trigger: "blur" }
          ],
          apiKey: [
            { required: true, message: "API key不能为空", trigger: "blur" }
          ],
        }
      };
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询AI模型配置列表 */
      getList() {
        this.loading = true;
        listAiModel(this.queryParams).then(response => {
          this.aimodelList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          id: null,
          name: null,
          model: null,
          baseUrl: null,
          apiKey: null,
          maxTokens: null,
          temperature: null,
          isDefault: null,
          status: null,
          remark: null,
          createBy: null,
          createTime: null,
          updateBy: null,
          updateTime: null
        };
        this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.resetForm("queryForm");
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.id)
        this.single = selection.length!==1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加AI模型配置";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const id = row.id || this.ids
        getAiModel(id).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "修改AI模型配置";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.id != null) {
              updateAiModel(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              addAiModel(this.form).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              });
            }
          }
        });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const ids = row.id || this.ids;
        this.$modal.confirm('是否确认删除AI模型配置编号为"' + ids + '"的数据项？').then(function() {
          return delAiModel(ids);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('patentSys/aimodel/export', {
          ...this.queryParams
        }, `aimodel_${new Date().getTime()}.xlsx`)
      }
    }
  };
  </script>

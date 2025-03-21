<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="申请号" prop="ApplicationNumber">
        <el-input
          v-model="queryParams.ApplicationNumber"
          placeholder="请输入申请号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公开号" prop="PublicationNumber">
        <el-input
          v-model="queryParams.PublicationNumber"
          placeholder="请输入公开号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="IPC号" prop="IPCClassification">
        <el-input
          v-model="queryParams.IPCClassification"
          placeholder="请输入IPC号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请人" prop="Applicant">
        <el-input
          v-model="queryParams.Applicant"
          placeholder="请输入申请人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发明人" prop="Inventor">
        <el-input
          v-model="queryParams.Inventor"
          placeholder="请输入发明人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发明名称" prop="InventionName">
        <el-input
          v-model="queryParams.InventionName"
          placeholder="请输入发明名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
<!--      <el-form-item label="邮编" prop="ApplicantPostalCode">-->
<!--        <el-input-->
<!--          v-model="queryParams.ApplicantPostalCode"-->
<!--          placeholder="请输入邮编"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="国别" prop="ApplicantCountry">-->
<!--        <el-input-->
<!--          v-model="queryParams.ApplicantCountry"-->
<!--          placeholder="请输入国别"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
      <el-form-item label="代理人" prop="Agent">
        <el-input
          v-model="queryParams.Agent"
          placeholder="请输入代理人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="代理机构" prop="Agency">
        <el-input
          v-model="queryParams.Agency"
          placeholder="请输入代理机构"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="文献类型" prop="DocumentType">
        <el-select v-model="queryParams.DocumentType" placeholder="请选择文献类型" clearable>
          <el-option
            v-for="dict in dict.type.patent_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="申请日" prop="ApplicationDate">
        <el-date-picker clearable
                        v-model="queryParams.ApplicationDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择申请日">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="公开日" prop="PublicationDate">
        <el-date-picker clearable
                        v-model="queryParams.PublicationDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择公开日">
        </el-date-picker>
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
          v-hasPermi="['patentSys:patents:add']"
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
          v-hasPermi="['patentSys:patents:edit']"
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
          v-hasPermi="['patentSys:patents:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['patentSys:patents:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="patentsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="发明名称" align="center" prop="inventionName" min-width="200" />
      <el-table-column label="申请人" align="center" prop="applicant" />
      <el-table-column label="发明人" align="center" prop="inventor" />
<!--      <el-table-column label="申请号" align="center" prop="applicationNumber" />-->
      <el-table-column label="公开号" align="center" prop="publicationNumber" />
<!--      <el-table-column label="申请日" align="center" prop="applicationDate">-->
<!--        <template slot-scope="scope">-->
<!--          <span>{{ parseTime(scope.row.ApplicationDate, '{y}-{m}-{d}') }}</span>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column label="公开日" align="center" prop="publicationDate">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.PublicationDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="IPC号" align="center" prop="ipcclassification" />
<!--      <el-table-column label="邮编" align="center" prop="applicantPostalCode" />-->
<!--      <el-table-column label="代理人" align="center" prop="agent" />-->
<!--      <el-table-column label="代理机构" align="center" prop="agency" />-->
      <el-table-column label="文献类型" align="center" prop="documentType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.patent_type" :value="scope.row.documentType"/>
        </template>
      </el-table-column>
<!--      <el-table-column label="国别" align="center" prop="applicantCountry" />-->
<!--      <el-table-column label="摘要" align="center" prop="abstract" />-->
<!--      <el-table-column label="专利唯一标识符" align="center" prop="patentId" />-->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['patentSys:patents:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['patentSys:patents:remove']"
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

    <!-- 添加或修改专利数据对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="申请号" prop="ApplicationNumber">
          <el-input v-model="form.ApplicationNumber" placeholder="请输入申请号" />
        </el-form-item>
        <el-form-item label="公开号" prop="PublicationNumber">
          <el-input v-model="form.PublicationNumber" placeholder="请输入公开号" />
        </el-form-item>
        <el-form-item label="申请日" prop="ApplicationDate">
          <el-date-picker clearable
            v-model="form.ApplicationDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择申请日">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="公开日" prop="PublicationDate">
          <el-date-picker clearable
            v-model="form.PublicationDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择公开日">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="IPC号" prop="IPCClassification">
          <el-input v-model="form.IPCClassification" placeholder="请输入IPC号" />
        </el-form-item>
        <el-form-item label="申请人" prop="Applicant">
          <el-input v-model="form.Applicant" placeholder="请输入申请人" />
        </el-form-item>
        <el-form-item label="发明人" prop="Inventor">
          <el-input v-model="form.Inventor" placeholder="请输入发明人" />
        </el-form-item>
        <el-form-item label="发明名称" prop="InventionName">
          <el-input v-model="form.InventionName" placeholder="请输入发明名称" />
        </el-form-item>
        <el-form-item label="邮编" prop="ApplicantPostalCode">
          <el-input v-model="form.ApplicantPostalCode" placeholder="请输入邮编" />
        </el-form-item>
        <el-form-item label="代理人" prop="Agent">
          <el-input v-model="form.Agent" placeholder="请输入代理人" />
        </el-form-item>
        <el-form-item label="代理机构" prop="Agency">
          <el-input v-model="form.Agency" placeholder="请输入代理机构" />
        </el-form-item>
        <el-form-item label="文献类型" prop="DocumentType">
          <el-select v-model="form.DocumentType" placeholder="请选择文献类型">
            <el-option
              v-for="dict in dict.type.patent_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="国别" prop="ApplicantCountry">
          <el-input v-model="form.ApplicantCountry" placeholder="请输入国别" />
        </el-form-item>
        <el-form-item label="摘要" prop="Abstract">
          <el-input v-model="form.Abstract" type="textarea" placeholder="请输入内容" />
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
import { listPatents, getPatents, delPatents, addPatents, updatePatents } from "@/api/patentSys/patents";

export default {
  name: "Patents",
  dicts: ['patent_type'],
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
      // 专利数据表格数据
      patentsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ApplicationNumber: null,
        PublicationNumber: null,
        ApplicationDate: null,
        PublicationDate: null,
        IPCClassification: null,
        Applicant: null,
        Inventor: null,
        InventionName: null,
        ApplicantPostalCode: null,
        Agent: null,
        Agency: null,
        DocumentType: null,
        ApplicantCountry: null,
        Abstract: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        ApplicationNumber: [
          { required: true, message: "申请号不能为空", trigger: "blur" }
        ],
        PublicationNumber: [
          { required: true, message: "公开号不能为空", trigger: "blur" }
        ],
        ApplicationDate: [
          { required: true, message: "申请日不能为空", trigger: "blur" }
        ],
        PublicationDate: [
          { required: true, message: "公开日不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询专利数据列表 */
    getList() {
      this.loading = true;
      listPatents(this.queryParams).then(response => {
        this.patentsList = response.rows;
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
        ApplicationNumber: null,
        PublicationNumber: null,
        ApplicationDate: null,
        PublicationDate: null,
        IPCClassification: null,
        Applicant: null,
        Inventor: null,
        InventionName: null,
        ApplicantPostalCode: null,
        Agent: null,
        Agency: null,
        DocumentType: null,
        ApplicantCountry: null,
        Abstract: null,
        patentId: null
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
      this.ids = selection.map(item => item.patentId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加专利数据";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const patentId = row.patentId || this.ids
      getPatents(patentId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改专利数据";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.patentId != null) {
            updatePatents(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addPatents(this.form).then(response => {
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
      const patentIds = row.patentId || this.ids;
      this.$modal.confirm('是否确认删除专利数据编号为"' + patentIds + '"的数据项？').then(function() {
        return delPatents(patentIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('patentSys/patents/export', {
        ...this.queryParams
      }, `patents_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

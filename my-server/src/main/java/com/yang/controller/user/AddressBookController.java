package com.yang.controller.user;

import com.yang.context.BaseContext;
import com.yang.entity.AddressBook;
import com.yang.result.Result;
import com.yang.service.AddressBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Tag( name= "地址簿接口")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/address/{id}")
    @Operation(summary = "根据用户id获取地址")
    public Result<List<AddressBook>> list(@PathVariable Long id){
        AddressBook addressBook = AddressBook.builder().userId(id).build();
        return Result.success(addressBookService.list(addressBook));
    }

    @PostMapping("/insert")
    @Operation(summary = "根据用户id添加地址")
    public Result<Integer> insert(@RequestBody AddressBook addressBook){
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return Result.success();
    }

    @GetMapping("/getbyid/{id}")
    @Operation(summary = "根据地址id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id){
        return Result.success(addressBookService.getById(id));
    }

    @PostMapping("/update")
    @Operation(summary = "根据地址id更新")
    public Result update(@RequestBody AddressBook addressBook){
        addressBookService.update(addressBook);
        return  Result.success();
    }

    @PostMapping("/default")
    @Operation(summary = "根据用户id设置默认地址")
    public Result updateIsDefaultByUserId(@RequestBody AddressBook a){
        addressBookService.updateIsDefaultByUserId(a);
        return  Result.success();
    }

    @GetMapping("/delete/{id}")
    @Operation(summary = "根据地址id删除")
    public Result deleteById(@PathVariable Long id){
        addressBookService.deleteById(id);
        return  Result.success();
    }

    @GetMapping("/getdefault")
    @Operation(summary = "根据用户id获取默认地址")
    public Result<AddressBook> getDefault(){
        AddressBook a=new AddressBook();
        a.setIsDefault(1);
        a.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(a);
        if(list !=null && list.size()>0){
            return Result.success(list.get(0));
        }
        return Result.error("没有查询到默认地址");
    }

}
